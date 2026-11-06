package com.utn.tpi.ms2.refactor.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PrioridadTest {

    @Test
    @DisplayName("desdeNivel debe devolver ALTA, MEDIA y BAJA para 1, 2 y 3")
    void desdeNivelDebeDevolverLaPrioridadCorrespondiente() {
        assertEquals(Prioridad.ALTA, Prioridad.desdeNivel(1));
        assertEquals(Prioridad.MEDIA, Prioridad.desdeNivel(2));
        assertEquals(Prioridad.BAJA, Prioridad.desdeNivel(3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 4})
    @DisplayName("desdeNivel debe lanzar IllegalArgumentException para un nivel fuera de rango")
    void desdeNivelDebeLanzarExcepcionParaNivelInvalido(int nivel) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Prioridad.desdeNivel(nivel)
        );

        assertEquals("Prioridad invalida. Debe ser 1, 2 o 3", ex.getMessage());
    }
}
