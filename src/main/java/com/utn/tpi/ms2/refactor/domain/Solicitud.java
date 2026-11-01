package com.utn.tpi.ms2.refactor.domain;

import java.time.LocalDateTime;

public class Solicitud {
    private final Integer id;
    private final String tipo;
    private final String descripcion;
    private final Prioridad prioridad;
    private EstadoSolicitud estado;
    private final LocalDateTime fechaCreacion;
    private final Estudiante estudiante;
    private String responsableNombre;
    private String areaDestino;

    public Solicitud(Integer id, String tipo, String descripcion, Prioridad prioridad,
                     EstadoSolicitud estado, LocalDateTime fechaCreacion, Estudiante estudiante,
                     String responsableNombre, String areaDestino) {
        if (tipo == null || tipo.isBlank()) throw new IllegalArgumentException("El tipo es obligatorio");
        if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("La descripcion es obligatoria");
        if (estudiante == null) throw new IllegalArgumentException("El estudiante es obligatorio");

        this.id = id;
        this.tipo = tipo.trim();
        this.descripcion = descripcion.trim();
        this.prioridad = prioridad != null ? prioridad : Prioridad.MEDIA;
        this.estado = estado != null ? estado : EstadoSolicitud.PENDIENTE;
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
        this.estudiante = estudiante;
        this.responsableNombre = responsableNombre != null ? responsableNombre : "SIN_ASIGNAR";
        this.areaDestino = areaDestino != null ? areaDestino : "MESA_ENTRADAS";
    }

    public void actualizarEstado(EstadoSolicitud nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        this.estado = nuevoEstado;
    }

    public void asignarResponsable(String responsable, String area) {
        if (responsable == null || responsable.isBlank()) throw new IllegalArgumentException("Responsable invalido");
        if (area == null || area.isBlank()) throw new IllegalArgumentException("Area invalida");
        this.responsableNombre = responsable.trim();
        this.areaDestino = area.trim();
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
