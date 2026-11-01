package com.utn.tpi.ms2.legacy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudServiceLegacy {

    private static final String FILE_PATH = "solicitudes_db.txt";
    private int proximoId = 1;

    public SolicitudServiceLegacy() {
        cargarUltimoId();
    }

    // OPERACION 1: Registrar solicitud (Long Method, Long Parameter List, Primitive Obsession)
    @PostMapping
    public ResponseEntity<String> crearSolicitud(
            @RequestParam String tipo,
            @RequestParam String descripcion,
            @RequestParam int prioridad,
            @RequestParam String legajo,
            @RequestParam String nombreEstudiante,
            @RequestParam String emailEstudiante,
            @RequestParam String carrera) {

        // Validaciones repetitivas con Strings mágicos (Code Smell: Duplicated Code)
        if (tipo == null || tipo.trim().equals("")) {
            return ResponseEntity.badRequest().body("Error: El tipo no puede estar vacio");
        }
        if (descripcion == null || descripcion.trim().equals("")) {
            return ResponseEntity.badRequest().body("Error: La descripcion no puede estar vacia");
        }
        if (legajo == null || legajo.trim().equals("")) {
            return ResponseEntity.badRequest().body("Error: El legajo es obligatorio");
        }

        // Magic numbers para prioridades
        if (prioridad < 1 || prioridad > 3) {
            return ResponseEntity.badRequest().body("Error: Prioridad invalida. Debe ser 1, 2 o 3");
        }

        Solicitud s = new Solicitud();
        s.id = proximoId++;
        s.tipo = tipo;
        s.descripcion = descripcion;
        s.prioridad = prioridad;
        s.estado = "PENDIENTE"; // Magic String
        s.fechaCreacion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Estudiante est = new Estudiante(legajo, nombreEstudiante, emailEstudiante, carrera);
        s.estudiante = est;
        s.responsableNombre = "SIN_ASIGNAR";
        s.areaDestino = "MESA_ENTRADAS";

        // Persistencia directa sin abstracción (Violación de SRP y DIP)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String linea = s.id + ";" + s.tipo + ";" + s.prioridad + ";" + s.estado + ";"
                    + s.fechaCreacion + ";" + s.estudiante.legajo + ";" + s.estudiante.nombreCompleto + ";"
                    + s.estudiante.email + ";" + s.estudiante.carrera + ";" + s.responsableNombre + ";"
                    + s.areaDestino + ";" + s.descripcion.replace(";", ",");
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error de persistencia: " + e.getMessage());
        }

        return ResponseEntity.ok("Solicitud creada con ID: " + s.id);
    }

    // OPERACION 2: Cambiar estado
    @PutMapping("/{id}/estado")
    public ResponseEntity<String> cambiarEstado(@PathVariable int id, @RequestParam String nuevoEstado) {
        // Validacion repetida y acoplada
        if (!nuevoEstado.equals("PENDIENTE") && !nuevoEstado.equals("EN_REVISION")
                && !nuevoEstado.equals("APROBADA") && !nuevoEstado.equals("RECHAZADA")) {
            return ResponseEntity.badRequest().body("Estado invalido");
        }

        List<String> lineasActualizadas = new ArrayList<>();
        boolean encontrada = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] partes = line.split(";");
                if (Integer.parseInt(partes[0]) == id) {
                    partes[3] = nuevoEstado; // Posicion harcodeada en el archivo
                    lineasActualizadas.add(String.join(";", partes));
                    encontrada = true;
                } else {
                    lineasActualizadas.add(line);
                }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al leer archivo");
        }

        if (!encontrada) {
            return ResponseEntity.status(404).body("Solicitud no encontrada");
        }

        guardarTodasLasLineas(lineasActualizadas);
        return ResponseEntity.ok("Estado actualizado exitosamente");
    }

    // OPERACION 3: Asignar responsable
    @PutMapping("/{id}/responsable")
    public ResponseEntity<String> asignarResponsable(
            @PathVariable int id,
            @RequestParam String responsable,
            @RequestParam String area) {

        List<String> lineasActualizadas = new ArrayList<>();
        boolean encontrada = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] partes = line.split(";");
                if (Integer.parseInt(partes[0]) == id) {
                    partes[9] = responsable;
                    partes[10] = area;
                    lineasActualizadas.add(String.join(";", partes));
                    encontrada = true;
                } else {
                    lineasActualizadas.add(line);
                }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al leer archivo");
        }

        if (!encontrada) {
            return ResponseEntity.status(404).body("Solicitud no encontrada");
        }

        guardarTodasLasLineas(lineasActualizadas);
        return ResponseEntity.ok("Responsable asignado");
    }

    // OPERACION 4: Listar reporte y buscar/filtrar (Feature Envy / Bloater)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarReporte(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String area) {

        List<Map<String, Object>> reporte = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return ResponseEntity.ok(reporte);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length < 12) continue;

                String sEstado = p[3];
                String sArea = p[10];

                if (estado != null && !estado.equalsIgnoreCase(sEstado)) continue;
                if (area != null && !area.equalsIgnoreCase(sArea)) continue;

                // Armado manual del reporte (Feature Envy)
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", Integer.parseInt(p[0]));
                dto.put("tipo", p[1]);
                dto.put("prioridadTexto", p[2].equals("1") ? "ALTA" : (p[2].equals("2") ? "MEDIA" : "BAJA"));
                dto.put("estado", sEstado);
                dto.put("estudianteLegajo", p[5]);
                dto.put("estudianteNombre", p[6]);
                dto.put("responsable", p[9]);
                dto.put("area", sArea);

                reporte.add(dto);
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(reporte);
    }

    private void guardarTodasLasLineas(List<String> lineas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarUltimoId() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = 0;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length > 0) {
                    try {
                        int id = Integer.parseInt(p[0]);
                        if (id > maxId) maxId = id;
                    } catch (NumberFormatException ignored) {}
                }
            }
            this.proximoId = maxId + 1;
        } catch (IOException ignored) {}
    }
}