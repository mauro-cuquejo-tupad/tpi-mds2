package com.utn.tpi.ms2.refactor.domain.model;

public class Estudiante {
    private String legajo;
    private String nombreCompleto;
    private String email;
    private String carrera;

    public Estudiante(String legajo, String nombreCompleto, String email, String carrera) {
        this.legajo = legajo;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.carrera = carrera;
    }

    public String getLegajo() { return legajo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public String getCarrera() { return carrera; }
}