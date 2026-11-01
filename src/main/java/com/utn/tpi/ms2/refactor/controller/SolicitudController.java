package com.utn.tpi.ms2.refactor.controller;

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
    public ResponseEntity<?> registrar(@RequestBody CrearSolicitudRequestDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarSolicitud(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        try {
            return ResponseEntity.ok(service.cambiarEstado(id, nuevoEstado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/responsable")
    public ResponseEntity<?> asignarResponsable(@PathVariable Integer id,
                                                @RequestParam String responsable,
                                                @RequestParam String area) {
        try {
            return ResponseEntity.ok(service.asignarResponsable(id, responsable, area));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponseDto>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String area) {
        return ResponseEntity.ok(service.listarReporte(estado, area));
    }
}