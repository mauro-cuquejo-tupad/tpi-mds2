package com.utn.tpi.ms2.refactor.controller;

import com.utn.tpi.ms2.refactor.domain.Solicitud;
import com.utn.tpi.ms2.refactor.dto.CrearSolicitudRequestDto;
import com.utn.tpi.ms2.refactor.dto.SolicitudResponseDto;
import com.utn.tpi.ms2.refactor.service.SolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v2/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> crearSolicitud(@RequestBody CrearSolicitudRequestDto request) {
        try {
            Solicitud creada = service.crearSolicitud(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Solicitud creada con ID: " + creada.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable int id, @RequestParam String nuevoEstado) {
        try {
            service.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok("Estado actualizado exitosamente");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/responsable")
    public ResponseEntity<String> asignarResponsable(
            @PathVariable int id,
            @RequestParam String responsable,
            @RequestParam String area) {
        try {
            service.asignarResponsable(id, responsable, area);
            return ResponseEntity.ok("Responsable asignado");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponseDto>> listarReporte(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String area) {
        List<SolicitudResponseDto> reporte = service.listarReporte(estado, area)
                .stream()
                .map(SolicitudResponseDto::fromDomain)
                .toList();
        return ResponseEntity.ok(reporte);
    }
}
