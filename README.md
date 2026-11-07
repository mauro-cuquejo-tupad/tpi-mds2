# Sistema de Gestión de Solicitudes Académicas

Trabajo Práctico Integrador de **Metodología de Sistemas II**  
**Universidad Tecnológica Nacional (UTN) - Tecnicatura Universitaria en Programación (TUPaD)**  
Comisión: M25 C4-05 | Ciclo Lectivo: 2026

---

## 👥 Integrantes del Equipo

| Apellido y Nombre | Legajo |
|---|---|
| Cornejo, Diego Gastón | 53648 |
| Cuquejo, Mauro Maximiliano | 56370 |
| Dantur, Daniel Fernando Jesús | 101435 |
| Díaz de Quintana, Melisa Magalí | 35513 |

- **Profesor:** Sebastián Bruselario
- **Tutor:** Samuel Tocaimaza

---

## 🎯 Objetivo del Proyecto

El propósito de este trabajo práctico es aplicar metodologías profesionales de diagnóstico, refactorización y aseguramiento de la calidad sobre un sistema preexistente de gestión de solicitudes académicas. 

A partir de una versión base funcional con severos problemas de diseño y alta deuda técnica (*God Class*, *Primitive Obsession*, violación de SRP y DIP, acoplamiento directo a disco), se llevó a cabo una evolución iterativa aplicando principios **SOLID**, patrones de diseño y buenas prácticas de **Clean Code**, garantizando la estabilidad y preservación del comportamiento externo a través de pruebas automatizadas y revisiones técnicas de código (*Code Review*).

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java 17+ (recomendado Java 21 LTS)
- **Framework:** Spring Boot 3.5.6 (Spring Web)
- **Gestor de dependencias y construcción:** Gradle 9 (utilizando Gradle Wrapper)
- **Testing unitario y de integración:** JUnit 5 (Jupiter 6.0), Mockito, Spring Test / MockMvc

---

## 📋 Requisitos Previos

- **Java Development Kit (JDK):** Versión 17 o superior (verificar con `java -version`).
- **Git:** Para clonar el repositorio y gestionar ramas.
- **Gradle:** No requiere instalación previa; el proyecto incluye los ejecutables del Gradle Wrapper (`gradlew` para Linux/macOS y `gradlew.bat` para Windows).

---

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone git@github.com:mauro-cuquejo-tupad/tpi-mds2.git
cd tpi-mds2
```

### 2. Ejecutar la aplicación

- **En Linux / macOS:**
  ```bash
  ./gradlew bootRun
  ```
- **En Windows:**
  ```cmd
  .\gradlew.bat bootRun
  ```

El servidor iniciará en el puerto local por defecto: `http://localhost:8080`.

### 3. Ejecutar la suite de pruebas automatizadas

- **En Linux / macOS:**
  ```bash
  ./gradlew test
  ```
- **En Windows:**
  ```cmd
  .\gradlew.bat test
  ```

Los reportes HTML detallados de ejecución se generarán en `build/reports/tests/test/index.html`.

---

## 🏗️ Estructura del Proyecto

El código fuente se organiza en dos paquetes principales que conviven para permitir la verificación y contraste evolutivo:

```text
src/main/java/com/utn/tpi/ms2/
├── legacy/                     # Versión inicial con deuda técnica
│   ├── Estudiante.java
│   ├── Solicitud.java
│   └── SolicitudServiceLegacy.java
└── refactor/                   # Versión evolucionada desacoplada
    ├── controller/             # Capa web y controladores REST
    │   ├── GlobalExceptionHandler.java
    │   └── SolicitudController.java
    ├── domain/                 # Lógica pura del dominio de negocio
    │   ├── exception/          # Excepciones semánticas (PersistenciaException)
    │   ├── model/              # Entidades y Enums (EstadoSolicitud, Prioridad)
    │   └── repository/         # Interfaces y contratos (SolicitudRepository)
    ├── dto/                    # Objetos de transferencia de datos (Request y Response)
    │   ├── CrearSolicitudRequestDto.java
    │   └── SolicitudResponseDto.java
    ├── infrastructure/         # Implementación de adaptadores y persistencia
    │   └── persistence/
    │       └── ArchivoSolicitudRepository.java
    └── service/                # Capa de aplicación y reglas de negocio
        └── SolicitudService.java
```

### Persistencia
Ambas versiones utilizan como medio de almacenamiento un archivo de texto plano denominado `solicitudes_db.txt`, ubicado en la raíz de ejecución del proyecto. En la versión refactorizada, el acceso a este archivo se encuentra totalmente aislado tras la interfaz `SolicitudRepository`, permitiendo su reemplazo y facilitando la ejecución de pruebas unitarias con mocks o carpetas temporales (`@TempDir`).

---

## 🔄 Funcionalidades y Comparativa de Endpoints

El sistema provee 4 operaciones clave sobre las solicitudes académicas:
1. **Registrar solicitud** (tipo, descripción, prioridad, datos del estudiante).
2. **Cambiar estado** (PENDIENTE, EN_REVISION, APROBADA, RECHAZADA).
3. **Asignar responsable y área** de derivación.
4. **Listar solicitudes** con filtros opcionales por estado y por área.

### Tabla Comparativa de Endpoints

| Operación | Legacy (`/api/solicitudes`) | Refactorizada (`/api/v2/solicitudes`) |
|---|---|---|
| **Registrar solicitud** | `POST /api/solicitudes`<br>*(Parámetros en la URL vía Query Params)* | `POST /api/v2/solicitudes`<br>*(Cuerpo de la petición en JSON)* |
| **Cambiar estado** | `PUT /api/solicitudes/{id}/estado`<br>*(Query Param: `nuevoEstado`)* | `PUT /api/v2/solicitudes/{id}/estado`<br>*(Query Param: `nuevoEstado`)* |
| **Asignar responsable** | `PUT /api/solicitudes/{id}/responsable`<br>*(Query Params: `responsable`, `area`)* | `PUT /api/v2/solicitudes/{id}/responsable`<br>*(Query Params: `responsable`, `area`)* |
| **Listar y filtrar** | `GET /api/solicitudes`<br>*(Query Params opcionales: `estado`, `area`)* | `GET /api/v2/solicitudes`<br>*(Query Params opcionales: `estado`, `area`)* |

### Diferencias Clave entre Versiones

- **Formato y Status de Creación:**
  - *Legacy:* Recibe 7 parámetros sueltos en URL (`@RequestParam`), retorna status `200 OK` con un mensaje en texto plano (`"Solicitud creada con ID: X"`).
  - *Refactorizada:* Recibe un objeto estructurado (`CrearSolicitudRequestDto`) en formato JSON (`@RequestBody`), retorna status `201 Created` con el recurso creado en JSON (`SolicitudResponseDto`).
- **Manejo de Errores y Excepciones:**
  - *Legacy:* Control de errores disperso con bloques `try-catch` dentro de los métodos; retorna strings de error o status 500 no estructurados.
  - *Refactorizada:* Centralizado mediante `GlobalExceptionHandler` (`@RestControllerAdvice`). Retorna status HTTP semánticos:
    - `400 Bad Request` ante campos obligatorios vacíos o prioridades fuera de rango (`IllegalArgumentException`).
    - `404 Not Found` cuando el identificador solicitado no existe (`NoSuchElementException`).
    - `500 Internal Server Error` ante fallos de I/O o inconsistencias en los datos persistidos (`PersistenciaException`), sin filtrar trazas ni rutas internas al cliente.
- **Tipado estricto vs. Primitive Obsession:**
  - *Legacy:* Utiliza cadenas mágicas para estados (`"PENDIENTE"`, `"APROBADA"`) y enteros libres para prioridades (`1`, `2`, `3`).
  - *Refactorizada:* Modela el dominio con enumeraciones fuertemente tipadas (`EstadoSolicitud`, `Prioridad`).
- **Respuesta de Listados:**
  - *Legacy:* Construye manualmente un mapa genérico `Map<String, Object>` por cada línea, ignorando silenciosamente registros con menos de 12 campos.
  - *Refactorizada:* Devuelve una lista tipada `List<SolicitudResponseDto>` aplicando el método de fábrica `SolicitudResponseDto.desdeEntidad(s)`.

---

## 🏷️ Hitos y Trazabilidad (Git Tags)

El repositorio mantiene trazabilidad de cada fase de evolución a través de tags:

- `estado-inicial`: Código monolítico legacy de partida.
- `etapa-diagnostico`: Diagnóstico inicial de deuda técnica y code smells en `/docs`.
- `etapa-refactoring`: Separación de responsabilidades, arquitectura en capas, SOLID y Clean Code.
- `etapa-pruebas`: Primera suite de pruebas unitarias sobre `SolicitudService` con Mockito.
- `etapa-docs`: Checklist y registro formal de code review en `/docs`.
- `etapa-refactoring-v1.0.0`: Centralización de excepciones con `GlobalExceptionHandler` (JIRA-06).
- `etapa-refactoring-v1.0.1`: Eliminación de cadenas mágicas y validaciones centralizadas (JIRA-07).
- `etapa-refactoring-v1.0.2`: Cobertura de enums, DTOs y pruebas del repositorio con `@TempDir` (JIRA-08).
- `estado-refactorizado`: Cierre definitivo de la versión refactorizada (*etiquetado programado en JIRA-11*).

---

## 📚 Documentación y Enlaces

- **Diagnóstico Inicial de Deuda Técnica:** [docs/diagnostico_inicial.md](docs/diagnostico_inicial.md)
- **Checklist Técnico y Registro de Code Review:** [docs/code_review.md](docs/code_review.md)
- **Colección de Pruebas de Postman:** [docs/postman/tpi-mds2.postman_collection.json](docs/postman/tpi-mds2.postman_collection.json)
- **Informe Final en Formato Académico (PDF):** *[Enlace pendiente - a completar en JIRA-11]*
- **Video de Presentación y Defensa del TPI:** *[Enlace pendiente - a completar en JIRA-11]*
