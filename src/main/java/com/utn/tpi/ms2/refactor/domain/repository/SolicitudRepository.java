package com.utn.tpi.ms2.refactor.domain.repository;

import com.utn.tpi.ms2.refactor.domain.model.Solicitud;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository {
    Solicitud guardar(Solicitud solicitud);
    Optional<Solicitud> buscarPorId(Integer id);
    List<Solicitud> obtenerTodas();
    void actualizar(Solicitud solicitud);
}