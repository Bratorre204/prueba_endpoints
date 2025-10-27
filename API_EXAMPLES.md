# Ejemplos de Uso de la API - Sistema de Asistencia

## Configuración Base
- **Base URL**: `http://localhost:8081`
- **Content-Type**: `application/json`

## 1. Autenticación

### Login
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan.perez@unipaz.edu.co",
    "contrasena": "password123"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "usuario": {
      "id_usuario": 1,
      "nombre": "Juan",
      "apellido": "Pérez",
      "correo": "juan.perez@unipaz.edu.co"
    }
  }
}
```

## 2. Gestión de Asignaturas

### Crear Asignatura
```bash
curl -X POST http://localhost:8081/api/asignaturas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "codigo": "PROG001",
    "nombre": "Programación I",
    "descripcion": "Fundamentos de programación orientada a objetos",
    "abreviatura": "PROG I",
    "creditos": 3
  }'
```

### Listar Asignaturas
```bash
curl -X GET "http://localhost:8081/api/asignaturas?page=0&size=10"
```

### Buscar Asignaturas
```bash
curl -X GET "http://localhost:8081/api/asignaturas/buscar?query=programacion"
```

## 3. Gestión de Cursos

### Crear Curso (Profesor)
```bash
curl -X POST http://localhost:8081/api/cursos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "idAsignatura": 1,
    "nombre": "Programación I - Diurna",
    "periodo": "2024-2",
    "turno": "DIURNA",
    "seccion": "A",
    "aula": "Aula 101",
    "horario": "Lunes 8:00-10:00",
    "idProfesor": 1
  }'
```

### Obtener Cursos de una Asignatura
```bash
curl -X GET "http://localhost:8081/api/cursos/asignatura/1?periodo=2024-1"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "asignatura": {
      "id": 1,
      "codigo": "PROG001",
      "nombre": "Programación I"
    },
    "nombre": "Programación I - Diurna",
    "periodo": "2024-1",
    "turno": "DIURNA",
    "seccion": "A",
    "aula": "Aula 101",
    "horario": "Lunes 8:00-10:00",
    "profesor": {
      "id_usuario": 1,
      "nombre": "Juan",
      "apellido": "Pérez"
    }
  },
  {
    "id": 2,
    "asignatura": {
      "id": 1,
      "codigo": "PROG001",
      "nombre": "Programación I"
    },
    "nombre": "Programación I - Nocturna",
    "periodo": "2024-1",
    "turno": "NOCTURNA",
    "seccion": "A",
    "aula": "Aula 201",
    "horario": "Lunes 18:00-20:00",
    "profesor": {
      "id_usuario": 1,
      "nombre": "Juan",
      "apellido": "Pérez"
    }
  }
]
```

### Listar Cursos
```bash
curl -X GET "http://localhost:8081/api/cursos?periodo=2024-1&turno=MAÑANA&page=0&size=10"
```

### Inscribir Estudiante
```bash
curl -X POST http://localhost:8081/api/cursos/1/inscribir \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "idEstudiante": 2,
    "observaciones": "Estudiante transferido"
  }'
```

## 3. Gestión de Sesiones

### Crear Sesión (Profesor)
```bash
curl -X POST http://localhost:8081/api/sesiones/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "idProfesor": 1,
    "idCurso": 1,
    "latitudProfesor": 4.6097,
    "longitudProfesor": -74.0817,
    "radioProximidad": 50.0
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Sesión creada exitosamente",
  "data": {
    "id": 1,
    "profesor": {
      "id_usuario": 1,
      "nombre": "Juan",
      "apellido": "Pérez"
    },
    "curso": {
      "id": 1,
      "nombre": "Programación I"
    },
    "latitudProfesor": 4.6097,
    "longitudProfesor": -74.0817,
    "radioProximidad": 50.0,
    "fechaCreacion": "2024-01-15T08:00:00",
    "estado": "ABIERTA"
  }
}
```

### Obtener Sesiones Activas de un Curso
```bash
curl -X GET http://localhost:8081/api/sesiones/curso/1/activas
```

### Cerrar Sesión
```bash
curl -X PUT http://localhost:8081/api/sesiones/1/cerrar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 4. Registro de Asistencia

### Firmar Asistencia (Estudiante)
```bash
curl -X POST http://localhost:8081/api/asistencia/firmar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "idSesion": 1,
    "idEstudiante": 2,
    "latitudEstudiante": 4.6098,
    "longitudEstudiante": -74.0818,
    "observacion": "Llegué un poco tarde"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Asistencia registrada correctamente",
  "data": {
    "id": 1,
    "sesion": {
      "id": 1,
      "curso": {
        "nombre": "Programación I"
      }
    },
    "estudiante": {
      "id_usuario": 2,
      "nombre": "María",
      "apellido": "González"
    },
    "fechaFirma": "2024-01-15T08:15:00",
    "estado": "PRESENTE",
    "latitudEstudiante": 4.6098,
    "longitudEstudiante": -74.0818,
    "distanciaMetros": 12.5,
    "observacion": "Llegué un poco tarde"
  }
}
```

### Validar si Puede Firmar
```bash
curl -X GET "http://localhost:8081/api/asistencia/validar?idSesion=1&idEstudiante=2"
```

**Respuesta:**
```json
{
  "puedeFirmar": true,
  "mensaje": "El estudiante puede firmar",
  "sesion": {
    "id": 1,
    "estado": "ABIERTA",
    "curso": {
      "nombre": "Programación I"
    }
  }
}
```

### Verificar Disponibilidad de Sesión
```bash
curl -X GET http://localhost:8081/api/asistencia/sesion/1/disponible
```

## 5. Consultas y Reportes

### Historial de Asistencia del Estudiante
```bash
curl -X GET "http://localhost:8081/api/asistencia/estudiante/2?page=0&size=20"
```

### Estadísticas del Estudiante
```bash
curl -X GET "http://localhost:8081/api/asistencia/estudiante/2/estadisticas?idCurso=1"
```

**Respuesta:**
```json
{
  "totalSesiones": 15,
  "asistencias": 12,
  "ausencias": 2,
  "tardias": 1,
  "fueraRango": 0,
  "porcentajeAsistencia": 80.0,
  "curso": "Programación I"
}
```

### Reporte de Asistencia de Curso
```bash
curl -X GET "http://localhost:8081/api/reportes/curso/1/asistencia?fechaInicio=2024-01-01&fechaFin=2024-01-31"
```

**Respuesta:**
```json
{
  "curso": "Programación I",
  "periodo": "2024-1",
  "estudiantes": [
    {
      "nombre": "María González",
      "identificacion": "maria.gonzalez@unipaz.edu.co",
      "asistencias": 12,
      "ausencias": 3,
      "porcentaje": 80.0
    },
    {
      "nombre": "Carlos Rodríguez",
      "identificacion": "carlos.rodriguez@unipaz.edu.co",
      "asistencias": 14,
      "ausencias": 1,
      "porcentaje": 93.3
    }
  ],
  "estadisticas": {
    "totalSesiones": 15,
    "promedioAsistencia": 86.7
  }
}
```

### Dashboard General
```bash
curl -X GET "http://localhost:8081/api/reportes/dashboard?periodo=2024-1"
```

## 6. Gestión de Usuarios

### Buscar Usuario
```bash
curl -X GET "http://localhost:8081/api/usuarios/buscar?query=maria"
```

### Actualizar Perfil
```bash
curl -X PUT http://localhost:8081/api/usuarios/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "nombre": "María Elena",
    "apellido": "González",
    "programa": "Ingeniería de Sistemas"
  }'
```

## 7. Casos de Uso Comunes

### Flujo Completo: Crear asignatura, curso y sesión

1. **Crear asignatura:**
```bash
curl -X POST http://localhost:8081/api/asignaturas \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "PROG001",
    "nombre": "Programación I",
    "descripcion": "Fundamentos de programación",
    "creditos": 3
  }'
```

2. **Crear curso diurno:**
```bash
curl -X POST http://localhost:8081/api/cursos \
  -H "Content-Type: application/json" \
  -d '{
    "idAsignatura": 1,
    "nombre": "Programación I - Diurna",
    "periodo": "2024-1",
    "turno": "DIURNA",
    "seccion": "A",
    "aula": "Aula 101",
    "horario": "Lunes 8:00-10:00",
    "idProfesor": 1
  }'
```

3. **Crear curso nocturno:**
```bash
curl -X POST http://localhost:8081/api/cursos \
  -H "Content-Type: application/json" \
  -d '{
    "idAsignatura": 1,
    "nombre": "Programación I - Nocturna",
    "periodo": "2024-1",
    "turno": "NOCTURNA",
    "seccion": "A",
    "aula": "Aula 201",
    "horario": "Lunes 18:00-20:00",
    "idProfesor": 1
  }'
```

4. **Profesor crea sesión para curso diurno:**
```bash
curl -X POST http://localhost:8081/api/sesiones/crear \
  -H "Content-Type: application/json" \
  -d '{
    "idProfesor": 1,
    "idCurso": 1,
    "latitudProfesor": 4.6097,
    "longitudProfesor": -74.0817,
    "radioProximidad": 50.0
  }'
```

5. **Estudiante valida si puede firmar:**
```bash
curl -X GET "http://localhost:8081/api/asistencia/validar?idSesion=1&idEstudiante=2"
```

6. **Estudiante firma asistencia:**
```bash
curl -X POST http://localhost:8081/api/asistencia/firmar \
  -H "Content-Type: application/json" \
  -d '{
    "idSesion": 1,
    "idEstudiante": 2,
    "latitudEstudiante": 4.6098,
    "longitudEstudiante": -74.0818
  }'
```

7. **Profesor consulta estadísticas:**
```bash
curl -X GET http://localhost:8081/api/sesiones/1/estadisticas
```

8. **Profesor cierra sesión:**
```bash
curl -X PUT http://localhost:8081/api/sesiones/1/cerrar
```

## 8. Códigos de Error Comunes

### 400 Bad Request
```json
{
  "success": false,
  "message": "El estudiante no está inscrito en este curso",
  "data": null
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Sesión no encontrada",
  "data": null
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Token JWT inválido o expirado",
  "data": null
}
```

## 9. Notas Importantes

- **Coordenadas**: Usar formato decimal (ej: 4.6097 para latitud)
- **Radio de Proximidad**: Se especifica en metros
- **Estados de Asistencia**: PRESENTE, AUSENTE, TARDIO, FUERA_RANGO
- **Paginación**: Usar parámetros `page` y `size` en consultas
- **Fechas**: Formato ISO 8601 (YYYY-MM-DDTHH:mm:ss)
- **Autenticación**: Incluir token JWT en header Authorization
