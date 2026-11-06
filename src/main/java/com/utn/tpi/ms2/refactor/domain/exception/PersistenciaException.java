package com.utn.tpi.ms2.refactor.domain.exception;

/**
 * Error al leer o escribir datos en el medio de persistencia.
 * Pertenece al dominio para que las capas superiores no dependan
 * de detalles de infraestructura (IOException, archivos, etc.).
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}