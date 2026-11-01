# Etapa 3: Diagnóstico de Deuda Técnica y Code Smells

Para esta etapa, la consigna exige identificar al menos 8 *code smells* o debilidades técnicas presentes en el sistema base, detallando para cada uno: **evidencia concreta en el código**, **ubicación**, **impacto técnico**, **riesgo de mantenimiento** y **prioridad de corrección**.

A continuación, se presenta el relevamiento completo en formato tabular y analítico, listo para incluir en el informe.

## 1. Tabla Resumen de Code Smells Identificados

| N.º | Code Smell / Debilidad | Ubicación | Prioridad | Refactoring Asociado / Solución |
|---|---|---|---|---|
| 1 | **God Class / Large Class** | `SolicitudServiceLegacy.java` | **Alta** | *Extract Class*: división en Controller, Service y Repository. |
| 2 | **Long Method** | `SolicitudServiceLegacy.java` (`crearSolicitud`) | **Alta** | *Extract Method*: delegación de validación y persistencia. |
| 3 | **Long Parameter List** | `SolicitudServiceLegacy.java` (`crearSolicitud`) | **Alta** | *Introduce Parameter Object*: crear DTO de solicitud. |
| 4 | **Primitive Obsession** | `Solicitud.java` y `SolicitudServiceLegacy.java` | **Alta** | *Replace Type Code with Class/Enum*: `EstadoSolicitud`, `Prioridad`. |
| 5 | **Feature Envy** | `SolicitudServiceLegacy.java` (`listarReporte`) | **Media** | *Move Method*: delegar representación a `SolicitudResponseDto`. |
| 6 | **Violación de SRP y DIP** | `SolicitudServiceLegacy.java` (métodos I/O) | **Alta** | *Extract Interface* e Inyección de Dependencias (`SolicitudRepository`). |
| 7 | **Data Class / Anemic Domain Model** | `Estudiante.java` y `Solicitud.java` | **Media** | *Encapsulate Field* y centralizar invariantes de negocio. |
| 8 | **Duplicated Code & Magic Values** | `SolicitudServiceLegacy.java` (múltiples métodos) | **Media** | *Extract Method* / *Replace Magic Number with Symbolic Constant*. |

## 2. Fichas de Detalle Técnico por Code Smell

### 1. God Class / Large Class

- **Ubicación:** `com.utn.tpi.ms2.legacy.SolicitudServiceLegacy` (líneas 10 a 204).
- **Evidencia en código:** Una única clase contiene endpoints HTTP (`@RestController`), reglas de negocio, generación de identificadores autonuméricos (`proximoId`), formateo y lectura/escritura física en disco con `BufferedReader` y `BufferedWriter`.
- **Impacto:** Alta complejidad ciclomática y acoplamiento severo. Cualquier cambio funcional o de infraestructura obliga a modificar y retestear la misma clase.
- **Riesgo de mantenimiento:** Dificultad extrema para trabajar en equipo en paralelo (conflictos de merge continuos) y alta probabilidad de efectos colaterales no deseados.
- **Prioridad:** **Alta**.

### 2. Long Method

- **Ubicación:** `SolicitudServiceLegacy.java` → método `crearSolicitud(...)`.
- **Evidencia en código:** Método de más de 45 líneas continuas que valida 4 campos por separado, evalúa rangos de prioridad, formatea fechas con `SimpleDateFormat`, instancia dos objetos y escribe directamente en archivo.
- **Impacto:** Viola el principio de nivel único de abstracción (SLAP). Es difícil comprender a simple vista cuál es el flujo principal de la operación.
- **Riesgo de mantenimiento:** Propenso a errores al agregar nuevas validaciones; dificulta reutilizar la lógica de persistencia o validación en otros flujos.
- **Prioridad:** **Alta**.

### 3. Long Parameter List

- **Ubicación:** `SolicitudServiceLegacy.java` → firma de `crearSolicitud(...)`.

**Evidencia en código:**

```java
public ResponseEntity<String> crearSolicitud(
        @RequestParam String tipo,
        @RequestParam String descripcion,
        @RequestParam int prioridad,
        @RequestParam String legajo,
        @RequestParam String nombreEstudiante,
        @RequestParam String emailEstudiante,
        @RequestParam String carrera)
```

- **Evidencia en código:** El método recibe 7 parámetros primitivos independientes como `@RequestParam`.
- **Impacto:** Dificulta el consumo de la API, vuelve frágiles las llamadas y satura la firma del método.
- **Riesgo de mantenimiento:** Agregar un nuevo dato (por ejemplo, teléfono) rompe las firmas existentes y las pruebas asociadas.
- **Prioridad:** **Alta** (habilita directamente el refactoring requerido *Introduce Parameter Object*).

### 4. Primitive Obsession

- **Ubicación:** `Solicitud.java` (campos `estado`, `prioridad`) y métodos de control de `SolicitudServiceLegacy.java`.

**Evidencia en código:**

```java
public int prioridad; // 1: Alta, 2: Media, 3: Baja
public String estado; // "PENDIENTE", "EN_REVISION", "RESUELTO", "RECHAZADO"
```

```java
if (prioridad < 1 || prioridad > 3) {
        return ResponseEntity.badRequest()
            .body("Error: Prioridad invalida. Debe ser 1, 2 o 3");
}
```

```java
if (!nuevoEstado.equals("PENDIENTE") && !nuevoEstado.equals("EN_REVISION")
        && !nuevoEstado.equals("APROBADA") && !nuevoEstado.equals("RECHAZADA")) {
        return ResponseEntity.badRequest().body("Estado invalido");
}
```

- **Impacto:** Se delega la integridad del modelo a validaciones manuales de cadenas de texto y enteros, perdiendo el chequeo en tiempo de compilación.
- **Riesgo de mantenimiento:** Posibilidad de estados inconsistentes por errores de tipeo (ej. `"Pendiente"` vs `"PENDIENTE"`), dispersión de comparaciones condicionales repetitivas.
- **Prioridad:** **Alta**.

### 5. Feature Envy

- **Ubicación:** `SolicitudServiceLegacy.java` → método `listarReporte(...)`.

**Evidencia en código:**

```java
Map<String, Object> dto = new HashMap<>();

dto.put("prioridadTexto",
        p[2].equals("1") ? "ALTA" : (p[2].equals("2") ? "MEDIA" : "BAJA"));

        dto.put("estudianteNombre", p[6]);
```

- **Impacto:** El controlador inspecciona las posiciones de memoria/arreglo internas para decidir cómo formatear y presentar los datos de Estudiante y Solicitud, en lugar de dejar que las propias entidades o un DTO específico expongan su representación.
- **Riesgo de mantenimiento:** Si la estructura de Estudiante cambia, el reporte en el servicio se rompe.
- **Prioridad:** **Media** (habilita el refactoring requerido *Move Method*).

### 6. Violación de SRP (Single Responsibility) y DIP (Dependency Inversion)

- **Ubicación:** `SolicitudServiceLegacy.java` (instanciación directa de `FileWriter`, `BufferedReader` y constante `FILE_PATH`).

**Evidencia en código:**

```java
private static final String FILE_PATH = "solicitudes_db.txt";
```

**En cada método:**

```java
try (BufferedWriter bw = new BufferedWriter(...)) {
        // Escritura en archivo
        }
```

- **Impacto:** El servicio de aplicación depende directamente de un detalle de bajo nivel (archivo físico).
- **Riesgo de mantenimiento:** Imposibilidad de ejecutar pruebas unitarias puras (sin tocar disco) y dificultad total para migrar a una base de datos real (SQL/JPA).
- **Prioridad:** **Alta**.

### 7. Data Class / Anemic Domain Model

- **Ubicación:** `Estudiante.java` y `Solicitud.java`.
- **Evidencia en código:** Clases con todos sus atributos definidos como `public`, sin encapsulamiento ni comportamiento de negocio asociado.
- **Impacto:** No hay control de invariantes: cualquier clase externa puede mutar el estado o el legajo de un estudiante en cualquier momento dejándolo en un estado inválido.
- **Riesgo de mantenimiento:** Dispersión de la lógica de validación del dominio en controladores o servicios externos.
- **Prioridad:** **Media**.

### 8. Duplicated Code & Magic Values

- **Ubicación:** `SolicitudServiceLegacy.java` (métodos `crearSolicitud`, `cambiarEstado`, `asignarResponsable`).

**Evidencia en código:**

- Validaciones repetidas: `if (campo == null || campo.trim().equals(""))`.
- Índices mágicos de parseo CSV dispersos: `p[0]`, `p[3]`, `p[9]`, `p[10]`.
- Estados iniciales hardcodeados: `"SIN_ASIGNAR"`, `"MESA_ENTRADAS"`.

- **Impacto:** Si se altera el orden de las columnas en el archivo de texto, fallan silenciosamente múltiples operaciones independientes.
- **Riesgo de mantenimiento:** Alto riesgo de introducir inconsistencias al modificar un índice en un método y olvidar actualizarlo en otro.
- **Prioridad:** **Media**.