package com.utn.tpi.ms2.legacy;

public class Solicitud {
    public int id;
    public String tipo;
    public String descripcion;
    public int prioridad; // 1: Alta, 2: Media, 3: Baja
    public String estado;  // "PENDIENTE", "EN_REVISION", "RESUELTO", "RECHAZADO"
    public String fechaCreacion;
    public Estudiante estudiante;
    public String responsableNombre;
    public String areaDestino;

    public Solicitud() {}
}