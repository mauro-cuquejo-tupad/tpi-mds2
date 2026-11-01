package com.utn.tpi.ms2.refactor.dto;

import com.utn.tpi.ms2.refactor.domain.Solicitud;

// Refactoring: Move Method (resuelve Feature Envy encapsulando la proyección del reporte)
public record SolicitudResponseDto(
        int id,
        String tipo,
        String prioridadTexto,
        String estado,
        String estudianteLegajo,
        String estudianteNombre,
        String responsable,
        String area
) {
    public static SolicitudResponseDto fromDomain(Solicitud s) {
        return new SolicitudResponseDto(
                s.getId(),
                s.getTipo(),
                s.getPrioridad().name(),
                s.getEstado().name(),
                s.getEstudiante().getLegajo(),
                s.getEstudiante().getNombreCompleto(),
                s.getResponsableNombre(),
                s.getAreaDestino()
        );
    }
}
