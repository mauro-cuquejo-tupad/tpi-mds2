package com.utn.tpi.ms2.refactor.domain;

public enum EstadoSolicitud {
    PENDIENTE,
    EN_REVISION,
    APROBADA,
    RECHAZADA;

    public static EstadoSolicitud fromString(String valor) {
        try {
            return EstadoSolicitud.valueOf(valor.toUpperCase().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Estado invalido: " + valor);
        }
    }
}
