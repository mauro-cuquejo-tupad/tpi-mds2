package com.utn.tpi.ms2.refactor.domain.model;

import java.time.LocalDateTime;

public class Solicitud {
    private Integer id;
    private String tipo;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
    private LocalDateTime fechaCreacion;
    private Estudiante estudiante;
    private String responsableNombre;
    private String areaDestino;

    public Solicitud(Integer id, String tipo, String descripcion, Prioridad prioridad,
                     Estudiante estudiante, String responsableNombre, String areaDestino) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estudiante = estudiante;
        this.responsableNombre = responsableNombre;
        this.areaDestino = areaDestino;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Solicitud(Integer id, String tipo, String descripcion, Prioridad prioridad,
                     EstadoSolicitud estado, LocalDateTime fechaCreacion, Estudiante estudiante,
                     String responsableNombre, String areaDestino) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.estudiante = estudiante;
        this.responsableNombre = responsableNombre;
        this.areaDestino = areaDestino;
    }

    public void cambiarEstado(EstadoSolicitud nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void asignarResponsable(String responsable, String area) {
        this.responsableNombre = responsable;
        this.areaDestino = area;
    }

    public Integer getId() { return id; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
    public EstadoSolicitud getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Estudiante getEstudiante() { return estudiante; }
    public String getResponsableNombre() { return responsableNombre; }
    public String getAreaDestino() { return areaDestino; }
}