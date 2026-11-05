package com.utn.tpi.ms2.refactor.controller;

import com.utn.tpi.ms2.refactor.dto.CrearSolicitudRequestDto;
import com.utn.tpi.ms2.refactor.dto.SolicitudResponseDto;
import com.utn.tpi.ms2.refactor.service.SolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponseDto> registrar(@RequestBody CrearSolicitudRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarSolicitud(dto));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponseDto>
    cambiarEstado(@PathVariable Integer id,
                  @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(service.cambiarEstado(id, nuevoEstado));
    }


    @PutMapping("/{id}/responsable")
    public ResponseEntity<SolicitudResponseDto>
    asignarResponsable(@PathVariable Integer id,
                       @RequestParam String responsable,
                       @RequestParam String area) {

        return ResponseEntity.ok(service.asignarResponsable(id, responsable, area));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponseDto>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String area) {
        return ResponseEntity.ok(service.listarReporte(estado, area));
    }
}