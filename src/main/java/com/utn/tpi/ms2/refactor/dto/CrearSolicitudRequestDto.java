package com.utn.tpi.ms2.refactor.dto;

public class CrearSolicitudRequestDto {
    private String tipo;
    private String descripcion;
    private Integer prioridad;
    private String legajo;
    private String nombreEstudiante;
    private String emailEstudiante;
    private String carrera;

    public CrearSolicitudRequestDto() {}

    public CrearSolicitudRequestDto(String tipo, String descripcion, Integer prioridad,
                                    String legajo, String nombreEstudiante,
                                    String emailEstudiante, String carrera) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.legajo = legajo;
        this.nombreEstudiante = nombreEstudiante;
        this.emailEstudiante = emailEstudiante;
        this.carrera = carrera;
    }

    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Integer getPrioridad() { return prioridad; }
    public String getLegajo() { return legajo; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getEmailEstudiante() { return emailEstudiante; }
    public String getCarrera() { return carrera; }
}