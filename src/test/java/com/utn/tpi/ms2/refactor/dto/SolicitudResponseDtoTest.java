package com.utn.tpi.ms2.refactor.dto;

import com.utn.tpi.ms2.refactor.domain.model.EstadoSolicitud;
import com.utn.tpi.ms2.refactor.domain.model.Estudiante;
import com.utn.tpi.ms2.refactor.domain.model.Prioridad;
import com.utn.tpi.ms2.refactor.domain.model.Solicitud;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudResponseDtoTest {

    private Solicitud crearSolicitud() {
        return new Solicitud(
                7,
                "HOMOLOGACION",
                "Equivalencia de materias",
                Prioridad.ALTA,
                EstadoSolicitud.EN_REVISION,
                LocalDateTime.of(2026, 3, 10, 9, 30, 0),
                new Estudiante("50123", "Juan Perez", "juan@frba.utn.edu.ar", "TUP"),
                "Ana Gomez",
                "BEDELIA"
        );
    }

    @Test
    @DisplayName("desdeEntidad debe copiar todos los campos de la solicitud")
    void desdeEntidadDebeCopiarTodosLosCampos() {
        SolicitudResponseDto dto = SolicitudResponseDto.desdeEntidad(crearSolicitud());

        assertAll(
                () -> assertEquals(7, dto.getId()),
                () -> assertEquals("HOMOLOGACION", dto.getTipo()),
                () -> assertEquals("ALTA", dto.getPrioridadTexto()),
                () -> assertEquals("EN_REVISION", dto.getEstado()),
                () -> assertEquals("50123", dto.getEstudianteLegajo()),
                () -> assertEquals("Juan Perez", dto.getEstudianteNombre()),
                () -> assertEquals("Ana Gomez", dto.getResponsable()),
                () -> assertEquals("BEDELIA", dto.getArea())
        );
    }

    @Test
    @DisplayName("prioridadTexto debe contener el nombre de la prioridad")
    void prioridadTextoDebeContenerElNombreDeLaPrioridad() {
        Solicitud solicitud = new Solicitud(
                1,
                "CERTIFICADO",
                "Certificado de alumno regular",
                Prioridad.BAJA,
                new Estudiante("50124", "Maria Lopez", "maria@frba.utn.edu.ar", "TUP"),
                "SIN_ASIGNAR",
                "MESA_ENTRADAS"
        );

        SolicitudResponseDto dto = SolicitudResponseDto.desdeEntidad(solicitud);

        assertEquals(Prioridad.BAJA.name(), dto.getPrioridadTexto());
    }
}
