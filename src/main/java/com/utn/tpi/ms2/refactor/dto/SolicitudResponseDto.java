package com.utn.tpi.ms2.refactor.dto;

import com.utn.tpi.ms2.refactor.domain.model.Solicitud;

public class SolicitudResponseDto {
    private Integer id;
    private String tipo;
    private String prioridadTexto;
    private String estado;
    private String estudianteLegajo;
    private String estudianteNombre;
    private String responsable;
    private String area;

    public static SolicitudResponseDto desdeEntidad(Solicitud s) {
        SolicitudResponseDto dto = new SolicitudResponseDto();
        dto.id = s.getId();
        dto.tipo = s.getTipo();
        dto.prioridadTexto = s.getPrioridad().name();
        dto.estado = s.getEstado().name();
        dto.estudianteLegajo = s.getEstudiante().getLegajo();
        dto.estudianteNombre = s.getEstudiante().getNombreCompleto();
        dto.responsable = s.getResponsableNombre();
        dto.area = s.getAreaDestino();
        return dto;
    }

    public Integer getId() { return id; }
    public String getTipo() { return tipo; }
    public String getPrioridadTexto() { return prioridadTexto; }
    public String getEstado() { return estado; }
    public String getEstudianteLegajo() { return estudianteLegajo; }
    public String getEstudianteNombre() { return estudianteNombre; }
    public String getResponsable() { return responsable; }
    public String getArea() { return area; }
}