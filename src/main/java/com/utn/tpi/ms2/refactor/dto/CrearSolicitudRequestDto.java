package com.utn.tpi.ms2.refactor.dto;

// Refactoring: Introduce Parameter Object
public record CrearSolicitudRequestDto(
        String tipo,
        String descripcion,
        int prioridad,
        String legajo,
        String nombreEstudiante,
        String emailEstudiante,
        String carrera
) {}
