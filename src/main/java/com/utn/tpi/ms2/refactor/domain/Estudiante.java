package com.utn.tpi.ms2.refactor.domain;

public class Estudiante {
    private final String legajo;
    private final String nombreCompleto;
    private final String email;
    private final String carrera;

    public Estudiante(String legajo, String nombreCompleto, String email, String carrera) {
        if (legajo == null || legajo.isBlank()) {
            throw new IllegalArgumentException("El legajo del estudiante es obligatorio");
        }
        this.legajo = legajo.trim();
        this.nombreCompleto = nombreCompleto != null ? nombreCompleto.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.carrera = carrera != null ? carrera.trim() : "";
    }

    public String getLegajo() { return legajo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getCarrera() { return carrera; }
}
