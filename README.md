# Sistema de Asistencia con Geolocalización - Backend API

## Descripción
Sistema completo de gestión de asistencia estudiantil con geolocalización para instituciones educativas. Permite a los profesores crear sesiones de clase y a los estudiantes registrar su asistencia mediante coordenadas GPS.

## Tecnologías Utilizadas
- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Gradle**

## Estructura del Proyecto

### Modelos de Base de Datos
- **Usuario**: Información de profesores y estudiantes
- **Asignatura**: Materias generales (ej: "Programación I")
- **Curso**: Instancias específicas de asignaturas (ej: "Programación I - Diurna", "Programación I - Nocturna")
- **Sesion**: Sesiones de clase creadas por profesores
- **SesionDetalle**: Registro de asistencia de estudiantes
- **UsuarioCurso**: Relación entre usuarios y cursos (inscripciones)
- **EstadoAsistencia**: Enum para estados de asistencia

### Servicios Principales
- **SesionService**: Gestión de sesiones de clase
- **AsistenciaService**: Registro y consulta de asistencias
- **CursoService**: Gestión de cursos e inscripciones
- **AsignaturaService**: Gestión de asignaturas (materias generales)
- **ReporteService**: Generación de reportes y estadísticas
- **UsuarioService**: Gestión de usuarios

## Endpoints de la API

### 1. Controlador de Sesiones (`/api/sesiones`)

#### Profesor - Gestión de Sesiones
- `POST /api/sesiones/crear` - Crear nueva sesión
- `PUT /api/sesiones/{id}/cerrar` - Cerrar sesión
- `GET /api/sesiones/profesor/{idProfesor}` - Sesiones de un profesor
- `GET /api/sesiones/curso/{idCurso}/activas` - Sesiones activas de un curso
- `GET /api/sesiones/{id}` - Detalle de sesión
- `GET /api/sesiones/{id}/asistencia` - Reporte de asistencia
- `GET /api/sesiones/{id}/estadisticas` - Estadísticas de sesión
- `PUT /api/sesiones/{id}` - Actualizar sesión
- `DELETE /api/sesiones/{id}` - Eliminar sesión

### 2. Controlador de Asistencia (`/api/asistencia`)

#### Estudiante - Registro de Asistencia
- `POST /api/asistencia/firmar` - Firmar asistencia
- `GET /api/asistencia/estudiante/{idEstudiante}` - Historial de asistencia
- `GET /api/asistencia/estudiante/{idEstudiante}/estadisticas` - Estadísticas del estudiante
- `GET /api/asistencia/validar` - Validar si puede firmar
- `GET /api/asistencia/sesion/{idSesion}/disponible` - Verificar disponibilidad

### 3. Controlador de Cursos (`/api/cursos`)

#### Gestión de Cursos
- `GET /api/cursos` - Listar cursos (con filtros)
- `GET /api/cursos/{id}` - Detalle de curso
- `GET /api/cursos/{id}/estudiantes` - Estudiantes inscritos
- `GET /api/cursos/profesor/{idProfesor}` - Cursos de un profesor
- `GET /api/cursos/estudiante/{idEstudiante}` - Cursos de un estudiante
- `GET /api/cursos/asignatura/{idAsignatura}` - Cursos de una asignatura
- `POST /api/cursos` - Crear curso
- `POST /api/cursos/{id}/inscribir` - Inscribir estudiante
- `GET /api/cursos/periodos` - Períodos disponibles
- `GET /api/cursos/turnos` - Turnos disponibles

### 4. Controlador de Asignaturas (`/api/asignaturas`)

#### Gestión de Asignaturas
- `GET /api/asignaturas` - Listar asignaturas
- `GET /api/asignaturas/{id}` - Detalle de asignatura
- `GET /api/asignaturas/codigo/{codigo}` - Asignatura por código
- `GET /api/asignaturas/buscar` - Buscar asignaturas
- `POST /api/asignaturas` - Crear asignatura
- `PUT /api/asignaturas/{id}` - Actualizar asignatura
- `DELETE /api/asignaturas/{id}` - Eliminar asignatura

### 5. Controlador de Reportes (`/api/reportes`)

#### Reportes y Estadísticas
- `GET /api/reportes/curso/{idCurso}/asistencia` - Reporte de curso
- `GET /api/reportes/estudiante/{idEstudiante}/consolidado` - Reporte consolidado estudiante
- `GET /api/reportes/profesor/{idProfesor}/consolidado` - Reporte consolidado profesor
- `GET /api/reportes/exportar/curso/{idCurso}/excel` - Exportar a Excel
- `GET /api/reportes/dashboard` - Dashboard general

### 6. Controlador de Usuarios (`/api/usuarios`)

#### Gestión de Usuarios
- `GET /api/usuarios/{id}` - Perfil de usuario
- `GET /api/usuarios/buscar` - Buscar usuarios
- `PUT /api/usuarios/{id}` - Actualizar usuario

## Funcionalidades Principales

### 1. Creación de Sesiones
Los profesores pueden crear sesiones de clase proporcionando:
- ID del profesor
- ID del curso
- Coordenadas GPS (latitud, longitud)
- Radio de proximidad en metros

### 2. Registro de Asistencia
Los estudiantes pueden registrar su asistencia:
- Escaneando código QR o ingresando ID de sesión
- Proporcionando sus coordenadas GPS
- El sistema calcula la distancia y determina el estado:
  - **PRESENTE**: Dentro del radio permitido
  - **FUERA_RANGO**: Fuera del radio permitido

### 3. Cálculo de Distancias
Utiliza la fórmula de Haversine para calcular distancias geográficas precisas entre coordenadas GPS.

### 4. Reportes y Estadísticas
- Reportes por curso con porcentajes de asistencia
- Estadísticas individuales de estudiantes
- Reportes consolidados por profesor
- Dashboard con métricas generales

### 5. Validaciones
- Verificación de inscripción en curso
- Validación de sesión activa
- Prevención de firmas duplicadas
- Control de proximidad geográfica

## Configuración de Base de Datos

### Tablas Principales
```sql
-- Usuarios (profesores y estudiantes)
usuarios (id_usuario, nombre, apellido, correo, contrasena, programa)

-- Asignaturas (materias generales)
asignaturas (id, codigo, nombre, descripcion, abreviatura, creditos)

-- Cursos (instancias específicas de asignaturas)
cursos (id, id_asignatura, nombre, periodo, turno, seccion, aula, horario, id_profesor)

-- Sesiones de clase
sesiones (id, id_profesor, id_curso, latitud_profesor, longitud_profesor, radio_proximidad, fecha_creacion, fecha_cierre, estado)

-- Registro de asistencia
sesiones_detalle (id, id_sesion, id_estudiante, fecha_firma, estado, latitud_estudiante, longitud_estudiante, distancia_metros, observacion)

-- Inscripciones
usuarios_cursos (id, id_usuario, id_curso, estado, observaciones)
```

## Instalación y Configuración

### 1. Requisitos
- Java 21+
- PostgreSQL 12+
- Gradle 7+

### 2. Configuración de Base de Datos
Actualizar `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/asistencia_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

### 3. Ejecución
```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8081`

## Seguridad

- Autenticación JWT implementada
- Endpoints públicos para funcionalidad básica
- Validación de roles para operaciones específicas
- CORS habilitado para desarrollo

## Características Técnicas

- **Arquitectura REST**: API RESTful completa
- **Validación**: Validación de entrada con Bean Validation
- **Paginación**: Soporte para paginación en consultas
- **Transacciones**: Gestión transaccional con Spring
- **Auditoría**: Entidades con auditoría automática
- **Manejo de Errores**: Respuestas consistentes con ApiResponse

## Próximas Mejoras

- [ ] Implementación completa de exportación Excel
- [ ] Notificaciones push para estudiantes
- [ ] Integración con sistemas académicos
- [ ] Dashboard en tiempo real
- [ ] Reportes avanzados con gráficos
- [ ] API de administración
- [ ] Logs de auditoría detallados
