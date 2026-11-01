package com.utn.tpi.ms2.refactor.repository;

import com.utn.tpi.ms2.refactor.domain.EstadoSolicitud;
import com.utn.tpi.ms2.refactor.domain.Estudiante;
import com.utn.tpi.ms2.refactor.domain.Prioridad;
import com.utn.tpi.ms2.refactor.domain.Solicitud;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class ArchivoSolicitudRepository implements SolicitudRepository {

    private static final String FILE_PATH = "solicitudes_db.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public synchronized Solicitud guardar(Solicitud solicitud) {
        List<Solicitud> solicitudes = listarTodas();
        int nuevoId = solicitud.getId() == null ? calcularProximoId(solicitudes) : solicitud.getId();

        Solicitud persistida = new Solicitud(
                nuevoId,
                solicitud.getTipo(),
                solicitud.getDescripcion(),
                solicitud.getPrioridad(),
                solicitud.getEstado(),
                solicitud.getFechaCreacion(),
                solicitud.getEstudiante(),
                solicitud.getResponsableNombre(),
                solicitud.getAreaDestino()
        );

        solicitudes.removeIf(s -> Objects.equals(s.getId(), nuevoId));
        solicitudes.add(persistida);
        sobrescribirArchivo(solicitudes);
        return persistida;
    }

    @Override
    public Optional<Solicitud> buscarPorId(int id) {
        return listarTodas().stream().filter(s -> s.getId() == id).findFirst();
    }

    @Override
    public List<Solicitud> listarTodas() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        List<Solicitud> resultado = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    resultado.add(deserializarLinea(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer archivo de persistencia", e);
        }
        return resultado;
    }

    // Refactoring: Extract Method (Lógica de serialización y deserialización aislada)
    private Solicitud deserializarLinea(String linea) {
        String[] p = linea.split(";");
        int id = Integer.parseInt(p[0]);
        String tipo = p[1];
        Prioridad prioridad = Prioridad.fromNivel(Integer.parseInt(p[2]));
        EstadoSolicitud estado = EstadoSolicitud.fromString(p[3]);
        LocalDateTime fecha = LocalDateTime.parse(p[4], FORMATTER);
        Estudiante est = new Estudiante(p[5], p[6], p[7], p[8]);
        String responsable = p[9];
        String area = p[10];
        String desc = p.length > 11 ? p[11] : "";

        return new Solicitud(id, tipo, desc, prioridad, estado, fecha, est, responsable, area);
    }

    private String serializarSolicitud(Solicitud s) {
        return String.join(";",
                String.valueOf(s.getId()),
                s.getTipo(),
                String.valueOf(s.getPrioridad().getNivel()),
                s.getEstado().name(),
                s.getFechaCreacion().format(FORMATTER),
                s.getEstudiante().getLegajo(),
                s.getEstudiante().getNombreCompleto(),
                s.getEstudiante().getEmail(),
                s.getEstudiante().getCarrera(),
                s.getResponsableNombre(),
                s.getAreaDestino(),
                s.getDescripcion().replace(";", ",")
        );
    }

    private void sobrescribirArchivo(List<Solicitud> solicitudes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Solicitud s : solicitudes) {
                bw.write(serializarSolicitud(s));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir archivo", e);
        }
    }

    private int calcularProximoId(List<Solicitud> solicitudes) {
        return solicitudes.stream().mapToInt(Solicitud::getId).max().orElse(0) + 1;
    }
}