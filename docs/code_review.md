# 1. Checklist Técnico de Code Review

| Categoría | Criterio de Inspección | Cumple (Inicial) | Cumple (Refactor) | Justificación Técnica / Hallazgo |
|---|---|---|---|---|
| **Arquitectura y Diseño** | ¿Existe una clara separación de capas y responsabilidades (SRP)? | ❌ No | ✅ Sí | En el código inicial, el controlador centralizaba E/S, negocio y mapeo. Se dividió en Controller, Service y Repository. |
| **Arquitectura y Diseño** | ¿Las dependencias de alto nivel apuntan a abstracciones y no a detalles (DIP)? | ❌ No | ✅ Sí | En legacy se instanciaba FileWriter directamente. En refactor, SolicitudService depende de la interfaz SolicitudRepository. |
| **Clean Code** | ¿Se evita el uso de *Primitive Obsession* en entidades clave? | ❌ No | ✅ Sí | Se eliminaron int y String sueltos para estados/prioridades y se crearon los enums EstadoSolicitud y Prioridad. |
| **Clean Code** | ¿Los nombres de métodos, variables y clases revelan intención sin ambigüedades? | ⚠️ Parcial | ✅ Sí | Se eliminaron variables genéricas de arrays (p[0], p[3]), reemplazándolas por propiedades con nombres de negocio. |
| **Refactoring** | ¿Se evita la lista excesiva de parámetros en métodos públicos? | ❌ No | ✅ Sí | Se aplicó *Introduce Parameter Object* mediante CrearSolicitudRequestDto. |
| **Refactoring** | ¿La lógica de presentación o transformación está reubicada adecuadamente? | ❌ No | ✅ Sí | Se aplicó *Move Method*, transfiriendo el armado del reporte hacia SolicitudResponseDto.desdeEntidad(). |
| **Manejo de Errores** | ¿El control de errores utiliza excepciones semánticas en lugar de retorno silencioso? | ❌ No | ✅ Sí | Se reemplazó el control manual por IllegalArgumentException y NoSuchElementException controlados. |
| **Testing** | ¿La lógica crítica cuenta con cobertura unitaria automatizada aislada de E/S? | ❌ No | ✅ Sí | Se crearon 6 pruebas unitarias con JUnit 5 y Mockito sin acceder al archivo físico en disco. |

# 2. Registro de Observaciones y Feedback de Pares

**Pull Request:** `feature/JIRA-01-ImplementacionSolicitudService`

**Revisor Técnico:** Diego Cornejo / Melisa M. Diaz de Quintana

**Fecha:** 2026-10-15

**Estado:** **Aprobado con observaciones resueltas**

### Observación 1: Acoplamiento directo a disco en capa de aplicación

- **Archivo:** `SolicitudServiceLegacy.java`
- **Comentario:**

  > El método de creación abre streams a disco dentro de la lógica de negocio. Esto impide testearlo unitariamente y bloquea la concurrencia. Se debe desacoplar mediante un contrato de repositorio.

- **Acción correctiva:** Se definió la interfaz SolicitudRepository y su implementación ArchivoSolicitudRepository, permitiendo mockear la persistencia en las pruebas unitarias.

### Observación 2: Cantidad excesiva de parámetros sueltos

- **Archivo:** `SolicitudServiceLegacy.java` → método `crearSolicitud(...)`
- **Comentario:**

  > El endpoint recibe 7 parámetros @RequestParam. Si el formulario del estudiante suma campos, la firma del método crecerá desproporcionadamente.

- **Acción correctiva:** Se implementó CrearSolicitudRequestDto, empaquetando los datos de la solicitud y del estudiante.

### Observación 3: Estados y prioridades sin tipado formal (Primitive Obsession)

- **Archivo:** `Solicitud.java`
- **Comentario:**

  > Se están pasando cadenas libres como 'PENDIENTE' y enteros como 1 o 2. Si alguien envía un estado con mayúsculas distintas o números fuera de rango, el archivo se corrompe silenciosamente.

- **Acción correctiva:** Se implementaron los enums EstadoSolicitud y Prioridad con métodos de validación estáticos.

# 3. Criterios de Aceptación Técnica del Merge

Para dar por concluida la revisión técnica y autorizar el merge a la rama principal, se verificó el cumplimiento de los siguientes criterios:

1. **Paridad funcional:** Todas las operaciones presentes en la versión inicial se mantienen operativas en la versión refactorizada sin regresiones visibles.

2. **Suite en verde:** Ejecución exitosa de los 6 tests automatizados con Gradle (`./gradlew test`).

3. **Cero dependencias circulares:** El paquete `refactor.domain` no tiene dependencias hacia `refactor.infrastructure` ni hacia `refactor.controller`.

4. **Trazabilidad:** Código versionado y etiquetado adecuadamente en Git.