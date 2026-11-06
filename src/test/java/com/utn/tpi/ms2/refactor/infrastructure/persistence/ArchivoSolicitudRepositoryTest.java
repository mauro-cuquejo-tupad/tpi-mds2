package com.utn.tpi.ms2.refactor.infrastructure.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.utn.tpi.ms2.refactor.domain.exception.PersistenciaException;
import com.utn.tpi.ms2.refactor.domain.model.EstadoSolicitud;
import com.utn.tpi.ms2.refactor.domain.model.Estudiante;
import com.utn.tpi.ms2.refactor.domain.model.Prioridad;
import com.utn.tpi.ms2.refactor.domain.model.Solicitud;

class ArchivoSolicitudRepositoryTest {

    @TempDir
    Path directorioTemporal;

    private Path archivo;
    private ArchivoSolicitudRepository repository;

    @BeforeEach
    void setUp() {
        archivo = directorioTemporal.resolve("solicitudes_test.txt");
        repository = new ArchivoSolicitudRepository(archivo.toString());
    }

    private Solicitud crearSolicitud(String tipo, String descripcion) {
        return new Solicitud(
                null,
                tipo,
                descripcion,
                Prioridad.MEDIA,
                new Estudiante("50123", "Juan Perez", "juan@frba.utn.edu.ar", "TUP"),
                "SIN_ASIGNAR",
                "MESA_ENTRADAS"
        );
    }

    @Test
    @DisplayName("Sin archivo, obtenerTodas debe devolver una lista vacía")
    void obtenerTodasSinArchivoDebeDevolverListaVacia() {
        assertFalse(Files.exists(archivo));

        List<Solicitud> resultado = repository.obtenerTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("guardar debe asignar ID 1 a la primera solicitud y luego incrementar")
    void guardarDebeAsignarIdsIncrementales() {
        Solicitud primera = repository.guardar(crearSolicitud("HOMOLOGACION", "Primera"));
        Solicitud segunda = repository.guardar(crearSolicitud("CERTIFICADO", "Segunda"));
        Solicitud tercera = repository.guardar(crearSolicitud("RECLAMO", "Tercera"));

        assertEquals(1, primera.getId());
        assertEquals(2, segunda.getId());
        assertEquals(3, tercera.getId());
        assertEquals(3, repository.obtenerTodas().size());
    }

    @Test
    @DisplayName("Una solicitud guardada debe recuperarse con los mismos datos")
    void solicitudGuardadaDebeRecuperarseConLosMismosDatos() {
        Solicitud guardada = repository.guardar(crearSolicitud("HOMOLOGACION", "Equivalencia de materias"));

        Solicitud recuperada = repository.buscarPorId(guardada.getId()).orElseThrow();

        assertAll(
                () -> assertEquals(guardada.getId(), recuperada.getId()),
                () -> assertEquals("HOMOLOGACION", recuperada.getTipo()),
                () -> assertEquals("Equivalencia de materias", recuperada.getDescripcion()),
                () -> assertEquals(Prioridad.MEDIA, recuperada.getPrioridad()),
                () -> assertEquals(EstadoSolicitud.PENDIENTE, recuperada.getEstado()),
                // El archivo guarda la fecha con precisión de segundos
                () -> assertEquals(
                        guardada.getFechaCreacion().truncatedTo(ChronoUnit.SECONDS),
                        recuperada.getFechaCreacion()),
                () -> assertEquals("50123", recuperada.getEstudiante().getLegajo()),
                () -> assertEquals("Juan Perez", recuperada.getEstudiante().getNombreCompleto()),
                () -> assertEquals("juan@frba.utn.edu.ar", recuperada.getEstudiante().getEmail()),
                () -> assertEquals("TUP", recuperada.getEstudiante().getCarrera()),
                () -> assertEquals("SIN_ASIGNAR", recuperada.getResponsableNombre()),
                () -> assertEquals("MESA_ENTRADAS", recuperada.getAreaDestino())
        );
    }

    @Test
    @DisplayName("buscarPorId debe devolver Optional.empty() si el ID no existe")
    void buscarPorIdInexistenteDebeDevolverVacio() {
        repository.guardar(crearSolicitud("HOMOLOGACION", "Equivalencia de materias"));

        Optional<Solicitud> resultado = repository.buscarPorId(99);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("actualizar debe modificar solo la solicitud indicada")
    void actualizarDebeModificarSoloLaSolicitudIndicada() {
        repository.guardar(crearSolicitud("HOMOLOGACION", "Primera"));
        Solicitud segunda = repository.guardar(crearSolicitud("CERTIFICADO", "Segunda"));
        repository.guardar(crearSolicitud("RECLAMO", "Tercera"));

        segunda.cambiarEstado(EstadoSolicitud.APROBADA);
        segunda.asignarResponsable("Ana Gomez", "BEDELIA");
        repository.actualizar(segunda);

        Solicitud primera = repository.buscarPorId(1).orElseThrow();
        Solicitud actualizada = repository.buscarPorId(2).orElseThrow();
        Solicitud tercera = repository.buscarPorId(3).orElseThrow();

        assertAll(
                () -> assertEquals(3, repository.obtenerTodas().size()),
                () -> assertEquals(EstadoSolicitud.APROBADA, actualizada.getEstado()),
                () -> assertEquals("Ana Gomez", actualizada.getResponsableNombre()),
                () -> assertEquals("BEDELIA", actualizada.getAreaDestino()),
                () -> assertEquals(EstadoSolicitud.PENDIENTE, primera.getEstado()),
                () -> assertEquals("SIN_ASIGNAR", primera.getResponsableNombre()),
                () -> assertEquals("MESA_ENTRADAS", primera.getAreaDestino()),
                () -> assertEquals(EstadoSolicitud.PENDIENTE, tercera.getEstado()),
                () -> assertEquals("SIN_ASIGNAR", tercera.getResponsableNombre()),
                () -> assertEquals("MESA_ENTRADAS", tercera.getAreaDestino())
        );
    }

    @Test
    @DisplayName("Un ';' en la descripción debe guardarse como ','")
    void puntoYComaEnDescripcionDebeGuardarseComoComa() throws IOException {
        Solicitud guardada = repository.guardar(crearSolicitud("HOMOLOGACION", "Materias: AED; PROG; BD"));

        Solicitud recuperada = repository.buscarPorId(guardada.getId()).orElseThrow();

        assertEquals("Materias: AED, PROG, BD", recuperada.getDescripcion());
        assertTrue(Files.readString(archivo).contains("Materias: AED, PROG, BD"));
    }
    private static final String LINEA_VALIDA = 
        "1;HOMOLOGACION;2;PENDIENTE;2026-10-30 10:00:00;50123;Ana Test;ana@test.com;TUP;SIN_ASIGNAR;MESA_ENTRADAS;Prueba";

private ArchivoSolicitudRepository repoConContenido(Path dir, String contenido) throws IOException {
    Path archivo = dir.resolve("solicitudes.txt");
    Files.writeString(archivo, contenido + System.lineSeparator());
    return new ArchivoSolicitudRepository(archivo.toString());
}

@Test
void lineaValidaSeLeeSinErrores(@TempDir Path dir) throws IOException {
    assertEquals(1, repoConContenido(dir, LINEA_VALIDA).obtenerTodas().size());
}

@Test
void lineaConMenosDeDoceColumnasLanzaPersistenciaException(@TempDir Path dir) throws IOException {
    ArchivoSolicitudRepository repo = repoConContenido(dir, "1;HOMOLOGACION;2");
    assertThrows(PersistenciaException.class, repo::obtenerTodas);
}

@Test
void prioridadInvalidaLanzaPersistenciaException(@TempDir Path dir) throws IOException {
    ArchivoSolicitudRepository repo = repoConContenido(dir, LINEA_VALIDA.replace(";2;", ";9;"));
    assertThrows(PersistenciaException.class, repo::obtenerTodas);
}

@Test
void estadoInvalidoLanzaPersistenciaException(@TempDir Path dir) throws IOException {
    ArchivoSolicitudRepository repo = repoConContenido(dir, LINEA_VALIDA.replace("PENDIENTE", "XYZ"));
    assertThrows(PersistenciaException.class, repo::obtenerTodas);
}

@Test
void fechaInvalidaLanzaPersistenciaException(@TempDir Path dir) throws IOException {
    ArchivoSolicitudRepository repo = repoConContenido(dir, LINEA_VALIDA.replace("2026-10-30 10:00:00", "no-es-fecha"));
    assertThrows(PersistenciaException.class, repo::obtenerTodas);
}
}
