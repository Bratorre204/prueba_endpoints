# ✅ Campo Identificación Implementado

## 🎯 **Cambios Implementados**

He agregado el campo `identificacion` (cédula colombiana) tanto para profesores como estudiantes y actualizado todos los endpoints relacionados.

### **1. Modelo Usuario Actualizado**

```java
@Entity
@Table(name = "usuarios")
public class Usuario extends AuditoriaEntidad {
    // ... otros campos ...
    
    @Column(name = "identificacion", unique = true, nullable = false, length = 20)
    private String identificacion; // Cédula colombiana
}
```

### **2. DTO de Registro Actualizado**

```java
public class RegisterRequest {
    // ... otros campos ...
    
    @NotBlank(message = "La identificación es obligatoria")
    @Size(min = 7, max = 12, message = "La identificación debe tener entre 7 y 12 caracteres")
    private String identificacion;
}
```

### **3. Servicio de Autenticación Actualizado**

```java
public AuthResponse register(RegisterRequest request) {
    Usuario usuario = Usuario.builder()
        .nombre(request.getNombre())
        .apellido(request.getApellido())
        .correo(request.getCorreo())
        .contrasena(passwordEncoder.encode(request.getContrasena()))
        .identificacion(request.getIdentificacion()) // ✅ Nuevo campo
        .programa(request.getPrograma())
        .build();
    // ...
}
```

### **4. Endpoint de Estudiantes por Curso Actualizado**

**Respuesta anterior:**
```json
[
  {
    "id": 2,
    "identificacion": "maria.gonzalez@unipaz.edu.co", // ❌ Era el correo
    "nombre": "María",
    "email": "maria.gonzalez@unipaz.edu.co"
  }
]
```

**Respuesta nueva:**
```json
[
  {
    "id": 2,
    "identificacion": "87654321", // ✅ Cédula real
    "nombre": "María González", // ✅ Nombre completo
    "email": "maria.gonzalez@unipaz.edu.co"
  }
]
```

### **5. DTOs de Respuesta Actualizados**

#### **CursoListaResponse:**
```java
public static class ProfesorBasico {
    private Long id;
    private String identificacion; // ✅ Nuevo campo
    private String nombre;
    private String apellidos;
    private String correo;
}
```

#### **CursoCreadoResponse:**
```java
public static class ProfesorBasico {
    private Long id;
    private String identificacion; // ✅ Nuevo campo
    private String nombre;
    private String apellidos;
}
```

### **6. Base de Datos Actualizada**

```sql
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    identificacion VARCHAR(20) NOT NULL UNIQUE, -- ✅ Nuevo campo
    programa VARCHAR(100),
    -- ... otros campos ...
);
```

### **7. Datos de Ejemplo Actualizados**

```sql
INSERT INTO usuarios (nombre, apellido, correo, contrasena, identificacion, programa) VALUES 
('Juan', 'Pérez', 'juan.perez@unipaz.edu.co', '$2a$10$...', '12345678', 'Ingeniería de Sistemas'),
('María', 'González', 'maria.gonzalez@unipaz.edu.co', '$2a$10$...', '87654321', 'Ingeniería de Sistemas'),
('Carlos', 'Rodríguez', 'carlos.rodriguez@unipaz.edu.co', '$2a$10$...', '11223344', 'Ingeniería de Sistemas'),
('Ana', 'Martínez', 'ana.martinez@unipaz.edu.co', '$2a$10$...', '44332211', 'Ingeniería de Sistemas');
```

## 📋 **Ejemplos de Uso**

### **Registrar Estudiante con Identificación:**

**POST** `/auth/register`
```json
{
  "nombre": "María",
  "apellido": "González",
  "correo": "maria.gonzalez@unipaz.edu.co",
  "contrasena": "password123",
  "rol": "ESTUDIANTE",
  "identificacion": "87654321",
  "programa": "Ingeniería de Sistemas"
}
```

### **Registrar Profesor con Identificación:**

**POST** `/auth/register`
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan.perez@unipaz.edu.co",
  "contrasena": "password123",
  "rol": "PROFESOR",
  "identificacion": "12345678",
  "programa": "Ingeniería de Sistemas"
}
```

### **Listar Estudiantes de un Curso:**

**GET** `/api/cursos/1/estudiantes`
```json
[
  {
    "id": 2,
    "identificacion": "87654321",
    "nombre": "María González",
    "email": "maria.gonzalez@unipaz.edu.co"
  },
  {
    "id": 3,
    "identificacion": "11223344",
    "nombre": "Carlos Rodríguez",
    "email": "carlos.rodriguez@unipaz.edu.co"
  }
]
```

### **Listar Cursos del Profesor:**

**GET** `/api/cursos/profesor/1`
```json
[
  {
    "id": 1,
    "codigo": "603D12025A",
    "nombre": "Programación I - Diurna",
    "profesor": {
      "id": 1,
      "identificacion": "12345678", // ✅ Cédula del profesor
      "nombre": "Juan",
      "apellidos": "Pérez",
      "correo": "juan.perez@unipaz.edu.co"
    }
  }
]
```

## ✅ **Beneficios Implementados**

1. **✅ Identificación única**: Cédula colombiana como identificador único
2. **✅ Validación**: Entre 7 y 12 caracteres
3. **✅ Unicidad**: No se pueden duplicar identificaciones
4. **✅ Respuestas completas**: Todos los endpoints incluyen la identificación
5. **✅ Compatibilidad**: Mantiene compatibilidad con el sistema existente
6. **✅ Base de datos actualizada**: Esquema y datos de ejemplo actualizados

## 🔧 **Validaciones**

- **Identificación obligatoria** en el registro
- **Formato válido**: Entre 7 y 12 caracteres
- **Única**: No se puede repetir en la base de datos
- **Requerida**: Tanto para profesores como estudiantes

El sistema ahora maneja correctamente las cédulas colombianas como identificadores únicos para todos los usuarios.
