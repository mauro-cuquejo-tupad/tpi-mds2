package com.utn.tpi.ms2.refactor.service;

import com.utn.tpi.ms2.refactor.domain.model.*;
import com.utn.tpi.ms2.refactor.domain.repository.SolicitudRepository;
import com.utn.tpi.ms2.refactor.dto.CrearSolicitudRequestDto;
import com.utn.tpi.ms2.refactor.dto.SolicitudResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class SolicitudService {

    private static final String RESPONSABLE_POR_DEFECTO = "SIN_ASIGNAR";
    private static final String AREA_POR_DEFECTO = "MESA_ENTRADAS";

    private final SolicitudRepository repository;

    // DIP: Inyección de dependencias por constructor
    public SolicitudService(SolicitudRepository repository) {
        this.repository = repository;
    }

    public SolicitudResponseDto registrarSolicitud(CrearSolicitudRequestDto dto) {
        validarCreacion(dto);

        Estudiante estudiante = new Estudiante(
                dto.getLegajo(),
                dto.getNombreEstudiante(),
                dto.getEmailEstudiante(),
                dto.getCarrera()
        );

        Prioridad prioridad = Prioridad.desdeNivel(dto.getPrioridad());
        Solicitud nueva = new Solicitud(
                null,
                dto.getTipo(),
                dto.getDescripcion(),
                prioridad,
                estudiante,
                RESPONSABLE_POR_DEFECTO,
                AREA_POR_DEFECTO
        );

        Solicitud guardada = repository.guardar(nueva);
        return SolicitudResponseDto.desdeEntidad(guardada);
    }

    public SolicitudResponseDto cambiarEstado(Integer id, String nuevoEstadoTexto) {
        Solicitud solicitud = buscarORechazar(id);
        EstadoSolicitud estado = EstadoSolicitud.desdeTexto(nuevoEstadoTexto);
        solicitud.cambiarEstado(estado);
        repository.actualizar(solicitud);
        return SolicitudResponseDto.desdeEntidad(solicitud);
    }

    public SolicitudResponseDto asignarResponsable(Integer id, String responsable, String area) {
        validarObligatorio(responsable, "Responsable y area son obligatorios");
        validarObligatorio(area, "Responsable y area son obligatorios");
        Solicitud solicitud = buscarORechazar(id);
        solicitud.asignarResponsable(responsable, area);
        repository.actualizar(solicitud);
        return SolicitudResponseDto.desdeEntidad(solicitud);
    }

    public List<SolicitudResponseDto> listarReporte(String estadoFiltro, String areaFiltro) {
        return repository.obtenerTodas().stream()
                .filter(s -> estadoFiltro == null || s.getEstado().name().equalsIgnoreCase(estadoFiltro))
                .filter(s -> areaFiltro == null || s.getAreaDestino().equalsIgnoreCase(areaFiltro))
                .map(SolicitudResponseDto::desdeEntidad)
                .collect(Collectors.toList());
    }

    // Refactoring obligatorio: Extract Method
    private Solicitud buscarORechazar(Integer id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada con ID: " + id));
    }

    // Refactoring obligatorio: Extract Method
    private void validarCreacion(CrearSolicitudRequestDto dto) {
        validarObligatorio(dto.getTipo(), "El tipo de solicitud es obligatorio");
        validarObligatorio(dto.getDescripcion(), "La descripcion es obligatoria");
        validarObligatorio(dto.getLegajo(), "El legajo es obligatorio");
        if (dto.getPrioridad() == null) {
            throw new IllegalArgumentException("Prioridad invalida. Debe ser 1, 2 o 3");
        }
    }

    private void validarObligatorio(String valor, String mensajeError) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
    }
}