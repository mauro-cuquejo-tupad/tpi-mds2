package com.utn.tpi.ms2.refactor.service;

import com.utn.tpi.ms2.refactor.domain.model.EstadoSolicitud;
import com.utn.tpi.ms2.refactor.domain.model.Estudiante;
import com.utn.tpi.ms2.refactor.domain.model.Prioridad;
import com.utn.tpi.ms2.refactor.domain.model.Solicitud;
import com.utn.tpi.ms2.refactor.domain.repository.SolicitudRepository;
import com.utn.tpi.ms2.refactor.dto.CrearSolicitudRequestDto;
import com.utn.tpi.ms2.refactor.dto.SolicitudResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository repository;

    private SolicitudService service;

    @BeforeEach
    void setUp() {
        service = new SolicitudService(repository);
    }

    @Test
    @DisplayName("Debe registrar una solicitud correctamente cuando los datos son válidos")
    void debeRegistrarSolicitudCorrectamente() {
        CrearSolicitudRequestDto dto = new CrearSolicitudRequestDto(
                "HOMOLOGACION",
                "Equivalencia de materias",
                1,
                "50123",
                "Juan Perez",
                "juan@frba.utn.edu.ar",
                "TUP"
        );

        Solicitud solicitudGuardada = new Solicitud(
                1,
                dto.getTipo(),
                dto.getDescripcion(),
                Prioridad.ALTA,
                new Estudiante("50123", "Juan Perez", "juan@frba.utn.edu.ar", "TUP"),
                "SIN_ASIGNAR",
                "MESA_ENTRADAS"
        );

        when(repository.guardar(any(Solicitud.class))).thenReturn(solicitudGuardada);

        SolicitudResponseDto respuesta = service.registrarSolicitud(dto);

        assertNotNull(respuesta);
        assertEquals(1, respuesta.getId());
        assertEquals("HOMOLOGACION", respuesta.getTipo());
        assertEquals("ALTA", respuesta.getPrioridadTexto());
        assertEquals("PENDIENTE", respuesta.getEstado());
        verify(repository, times(1)).guardar(any(Solicitud.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el tipo de solicitud está vacío")
    void debeLanzarExcepcionSiTipoEstaVacio() {
        CrearSolicitudRequestDto dtoInvalido = new CrearSolicitudRequestDto(
                "",
                "Descripcion valida",
                1,
                "50123",
                "Juan Perez",
                "juan@frba.utn.edu.ar",
                "TUP"
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarSolicitud(dtoInvalido));
        assertEquals("El tipo de solicitud es obligatorio", ex.getMessage());
        verify(repository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la prioridad es inválida")
    void debeLanzarExcepcionSiPrioridadEsInvalida() {
        CrearSolicitudRequestDto dtoPrioridadInvalida = new CrearSolicitudRequestDto(
                "EXCEPCION_CORRELATIVAS",
                "Descripcion valida",
                5, // Invalido
                "50123",
                "Juan Perez",
                "juan@frba.utn.edu.ar",
                "TUP"
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarSolicitud(dtoPrioridadInvalida));
        assertEquals("Prioridad invalida. Debe ser 1, 2 o 3", ex.getMessage());
        verify(repository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la prioridad es nula")
    void debeLanzarExcepcionSiPrioridadEsNula() {
        CrearSolicitudRequestDto dtoPrioridadNula = new CrearSolicitudRequestDto(
                "EXCEPCION_CORRELATIVAS",
                "Descripcion valida",
                null,
                "50123",
                "Juan Perez",
                "juan@frba.utn.edu.ar",
                "TUP"
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarSolicitud(dtoPrioridadNula));
        assertEquals("Prioridad invalida. Debe ser 1, 2 o 3", ex.getMessage());
        verify(repository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe cambiar el estado de la solicitud de forma exitosa")
    void debeCambiarEstadoExitosamente() {
        Solicitud existente = new Solicitud(
                10,
                "REVISION_EXAMEN",
                "Pedido de revision",
                Prioridad.MEDIA,
                new Estudiante("50123", "Juan Perez", "juan@frba.utn.edu.ar", "TUP"),
                "SIN_ASIGNAR",
                "MESA_ENTRADAS"
        );

        when(repository.buscarPorId(10)).thenReturn(Optional.of(existente));

        SolicitudResponseDto respuesta = service.cambiarEstado(10, "EN_REVISION");

        assertEquals("EN_REVISION", respuesta.getEstado());
        verify(repository, times(1)).actualizar(existente);
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando se opera sobre un ID inexistente")
    void debeLanzarExcepcionSiIdNoExiste() {
        when(repository.buscarPorId(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.cambiarEstado(999, "APROBADA"));
        verify(repository, never()).actualizar(any());
    }

    @Test
    @DisplayName("Debe filtrar solicitudes por estado y área correctamente")
    void debeFiltrarSolicitudesPorEstadoYArea() {
        Solicitud s1 = new Solicitud(1, "A", "Desc", Prioridad.BAJA, new Estudiante("1", "A", "A", "TUP"), "Resp", "SISTEMAS");
        Solicitud s2 = new Solicitud(2, "B", "Desc", Prioridad.ALTA, new Estudiante("2", "B", "B", "TUP"), "Resp", "MESA_ENTRADAS");
        s2.cambiarEstado(EstadoSolicitud.APROBADA);

        when(repository.obtenerTodas()).thenReturn(List.of(s1, s2));

        List<SolicitudResponseDto> resultado = service.listarReporte("APROBADA", "MESA_ENTRADAS");

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).getId());
    }
}