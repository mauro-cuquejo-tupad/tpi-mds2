package com.utn.tpi.ms2.refactor.service;

import com.utn.tpi.ms2.refactor.domain.EstadoSolicitud;
import com.utn.tpi.ms2.refactor.domain.Estudiante;
import com.utn.tpi.ms2.refactor.domain.Prioridad;
import com.utn.tpi.ms2.refactor.domain.Solicitud;
import com.utn.tpi.ms2.refactor.dto.CrearSolicitudRequestDto;
import com.utn.tpi.ms2.refactor.repository.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SolicitudService {

    private final SolicitudRepository repository;

    // Inyección de dependencias por constructor (DIP)
    public SolicitudService(SolicitudRepository repository) {
        this.repository = repository;
    }

    public Solicitud crearSolicitud(CrearSolicitudRequestDto dto) {
        validarRequest(dto);

        Estudiante estudiante = new Estudiante(
                dto.legajo(),
                dto.nombreEstudiante(),
                dto.emailEstudiante(),
                dto.carrera()
        );

        Solicitud solicitud = new Solicitud(
                null,
                dto.tipo(),
                dto.descripcion(),
                Prioridad.fromNivel(dto.prioridad()),
                EstadoSolicitud.PENDIENTE,
                LocalDateTime.now(),
                estudiante,
                "SIN_ASIGNAR",
                "MESA_ENTRADAS"
        );

        return repository.guardar(solicitud);
    }

    public Solicitud cambiarEstado(int id, String nuevoEstadoStr) {
        Solicitud solicitud = buscarPorIdOError(id);
        EstadoSolicitud nuevoEstado = EstadoSolicitud.fromString(nuevoEstadoStr);
        solicitud.actualizarEstado(nuevoEstado);
        return repository.guardar(solicitud);
    }

    public Solicitud asignarResponsable(int id, String responsable, String area) {
        Solicitud solicitud = buscarPorIdOError(id);
        solicitud.asignarResponsable(responsable, area);
        return repository.guardar(solicitud);
    }

    public List<Solicitud> listarReporte(String estado, String area) {
        return repository.listarTodas().stream()
                .filter(s -> estado == null || s.getEstado().name().equalsIgnoreCase(estado.trim()))
                .filter(s -> area == null || s.getAreaDestino().equalsIgnoreCase(area.trim()))
                .toList();
    }

    // Refactoring: Extract Method
    private Solicitud buscarPorIdOError(int id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud con ID " + id + " no encontrada"));
    }

    private void validarRequest(CrearSolicitudRequestDto dto) {
        if (dto.tipo() == null || dto.tipo().isBlank()) throw new IllegalArgumentException("El tipo no puede estar vacio");
        if (dto.descripcion() == null || dto.descripcion().isBlank()) throw new IllegalArgumentException("La descripcion no puede estar vacia");
        if (dto.legajo() == null || dto.legajo().isBlank()) throw new IllegalArgumentException("El legajo es obligatorio");
    }
}
