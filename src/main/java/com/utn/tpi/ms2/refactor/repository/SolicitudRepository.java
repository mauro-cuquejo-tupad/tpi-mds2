package com.utn.tpi.ms2.refactor.repository;

import com.utn.tpi.ms2.refactor.domain.Solicitud;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository {
    Solicitud guardar(Solicitud solicitud);
    Optional<Solicitud> buscarPorId(int id);
    List<Solicitud> listarTodas();
}
