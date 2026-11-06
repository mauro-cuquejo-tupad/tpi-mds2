package com.utn.tpi.ms2.refactor.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class EstadoSolicitudTest {

    @ParameterizedTest
    @EnumSource(EstadoSolicitud.class)
    @DisplayName("desdeTexto debe aceptar los cuatro estados")
    void desdeTextoDebeAceptarTodosLosEstados(EstadoSolicitud estado) {
        assertEquals(estado, EstadoSolicitud.desdeTexto(estado.name()));
    }

    @Test
    @DisplayName("desdeTexto debe aceptar minúsculas y espacios alrededor")
    void desdeTextoDebeAceptarMinusculasYEspacios() {
        assertEquals(EstadoSolicitud.PENDIENTE, EstadoSolicitud.desdeTexto(" pendiente "));
        assertEquals(EstadoSolicitud.EN_REVISION, EstadoSolicitud.desdeTexto("en_revision"));
    }

    @Test
    @DisplayName("desdeTexto debe lanzar IllegalArgumentException para un texto desconocido")
    void desdeTextoDebeLanzarExcepcionParaTextoDesconocido() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> EstadoSolicitud.desdeTexto("CERRADA")
        );

        assertEquals("Estado invalido: CERRADA", ex.getMessage());
    }

    @Test
    @DisplayName("desdeTexto debe lanzar IllegalArgumentException para null")
    void desdeTextoDebeLanzarExcepcionParaNull() {
        assertThrows(IllegalArgumentException.class, () -> EstadoSolicitud.desdeTexto(null));
    }
}
