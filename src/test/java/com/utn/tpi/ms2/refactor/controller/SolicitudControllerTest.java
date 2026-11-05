package com.utn.tpi.ms2.refactor.controller;

import com.utn.tpi.ms2.refactor.service.SolicitudService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolicitudService service;

    @Test
    @DisplayName("Debe responder 404 cuando la solicitud no existe")
    void debeResponder404CuandoLaSolicitudNoExiste() throws Exception {
        when(service.cambiarEstado(999, "APROBADA"))
                .thenThrow(new NoSuchElementException("Solicitud no encontrada con ID: 999"));

        mockMvc.perform(put("/api/v2/solicitudes/999/estado")
                        .param("nuevoEstado", "APROBADA"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Solicitud no encontrada con ID: 999"));
    }

    @Test
    @DisplayName("Debe responder 400 cuando los datos de la solicitud son invalidos")
    void debeResponder400CuandoLosDatosSonInvalidos() throws Exception {
        when(service.registrarSolicitud(any()))
                .thenThrow(new IllegalArgumentException("Prioridad invalida. Debe ser 1, 2 o 3"));

        String cuerpo = """
                {
                  "tipo": "HOMOLOGACION",
                  "descripcion": "Prueba",
                  "prioridad": 5,
                  "legajo": "50123",
                  "nombreEstudiante": "Ana Test",
                  "emailEstudiante": "ana@test.com",
                  "carrera": "TUP"
                }
                """;

        mockMvc.perform(post("/api/v2/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Prioridad invalida. Debe ser 1, 2 o 3"));
    }
}
