package com.utn.tpi.ms2.refactor.infrastructure.persistence;

import com.utn.tpi.ms2.refactor.domain.model.*;
import com.utn.tpi.ms2.refactor.domain.repository.SolicitudRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class ArchivoSolicitudRepository implements SolicitudRepository {

    static final String RUTA_ARCHIVO_POR_DEFECTO = "solicitudes_db.txt";
    private static final String SEPARADOR = ";";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String rutaArchivo;

    public ArchivoSolicitudRepository() {
        this(RUTA_ARCHIVO_POR_DEFECTO);
    }

    public ArchivoSolicitudRepository(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public synchronized Solicitud guardar(Solicitud solicitud) {
        int nuevoId = generarSiguienteId();
        Solicitud nueva = new Solicitud(
                nuevoId,
                solicitud.getTipo(),
                solicitud.getDescripcion(),
                solicitud.getPrioridad(),
                solicitud.getEstudiante(),
                solicitud.getResponsableNombre(),
                solicitud.getAreaDestino()
        );

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            bw.write(mapearEntidadALinea(nueva));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error de E/S al guardar solicitud en archivo", e);
        }
        return nueva;
    }

    @Override
    public Optional<Solicitud> buscarPorId(Integer id) {
        return obtenerTodas().stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    @Override
    public List<Solicitud> obtenerTodas() {
        List<Solicitud> listado = new ArrayList<>();
        File file = new File(rutaArchivo);
        if (!file.exists()) return listado;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    listado.add(mapearLineaAEntidad(linea));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer archivo de solicitudes", e);
        }
        return listado;
    }

    @Override
    public synchronized void actualizar(Solicitud solicitudModificada) {
        List<Solicitud> solicitudes = obtenerTodas();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            for (Solicitud s : solicitudes) {
                Solicitud item = s.getId().equals(solicitudModificada.getId()) ? solicitudModificada : s;
                bw.write(mapearEntidadALinea(item));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar archivo", e);
        }
    }

    private String mapearEntidadALinea(Solicitud s) {
        return String.join(SEPARADOR,
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

    private Solicitud mapearLineaAEntidad(String linea) {
        String[] p = linea.split(SEPARADOR);
        Estudiante estudiante = new Estudiante(p[5], p[6], p[7], p[8]);
        return new Solicitud(
                Integer.parseInt(p[0]),
                p[1],
                p[11],
                Prioridad.desdeNivel(Integer.parseInt(p[2])),
                EstadoSolicitud.desdeTexto(p[3]),
                LocalDateTime.parse(p[4], FORMATTER),
                estudiante,
                p[9],
                p[10]
        );
    }

    private int generarSiguienteId() {
        return obtenerTodas().stream().mapToInt(Solicitud::getId).max().orElse(0) + 1;
    }
}