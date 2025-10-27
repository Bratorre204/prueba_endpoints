# Ejemplos de Autenticación - AuthController Corregido

## Endpoints Corregidos

### 1. Login
**POST** `/auth/login`

**Request:**
```json
{
  "correo": "juan.perez@unipaz.edu.co",
  "contrasena": "password123"
}
```

**Response Exitosa (200):**
```json
{
  "success": true,
  "message": "Login exitoso!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "nombre": "Juan",
    "apellido": "Pérez",
    "correo": "juan.perez@unipaz.edu.co",
    "programa": "Ingeniería de Sistemas"
  }
}
```

**Response Error (401):**
```json
{
  "success": false,
  "message": "Correo o contraseña inválidos",
  "data": null
}
```

### 2. Register
**POST** `/auth/register`

**Request:**
```json
{
  "nombre": "María",
  "apellido": "González",
  "correo": "maria.gonzalez@unipaz.edu.co",
  "contrasena": "password123",
  "rol": "ESTUDIANTE",
  "programa": "Ingeniería de Sistemas"
}
```

**Response Exitosa (200):**
```json
{
  "success": true,
  "message": "Registro exitoso!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "nombre": "María",
    "apellido": "González",
    "correo": "maria.gonzalez@unipaz.edu.co",
    "programa": "Ingeniería de Sistemas"
  }
}
```

**Response Error (400):**
```json
{
  "success": false,
  "message": "Ya existe un usuario con este correo",
  "data": null
}
```

## Problemas Corregidos

### ✅ **1. Inconsistencia en ApiResponse**
- **Antes**: Usaba `ApiResponse<AuthResponse>` con genéricos
- **Después**: Usa `ApiResponse` simple sin genéricos
- **Beneficio**: Evita errores de compilación

### ✅ **2. Campo `status` vs `success`**
- **Antes**: Usaba `.status(true)` pero el campo era `success`
- **Después**: Usa `.success(true)` correctamente
- **Beneficio**: Consistencia en la respuesta

### ✅ **3. Manejo de Excepciones**
- **Antes**: Solo `login` tenía manejo de excepciones
- **Después**: Ambos métodos tienen manejo completo
- **Beneficio**: Respuestas consistentes en caso de error

### ✅ **4. Validaciones Agregadas**
- **Antes**: No había validaciones
- **Después**: Validaciones completas con Bean Validation
- **Beneficio**: Mejor seguridad y UX

### ✅ **5. Campo `latitud` Eliminado**
- **Antes**: Campo innecesario en LoginRequest
- **Después**: Solo campos necesarios para autenticación
- **Beneficio**: API más limpia y enfocada

### ✅ **6. Rutas Corregidas**
- **Antes**: `@PostMapping("login")` (sin barra)
- **Después**: `@PostMapping("/login")` (con barra)
- **Beneficio**: Consistencia en las rutas

## Validaciones Implementadas

### LoginRequest
- `correo`: Obligatorio y formato de email válido
- `contrasena`: Obligatoria

### RegisterRequest
- `nombre`: Obligatorio, máximo 100 caracteres
- `apellido`: Obligatorio, máximo 100 caracteres
- `correo`: Obligatorio y formato de email válido
- `contrasena`: Obligatoria, mínimo 6 caracteres
- `rol`: Obligatorio
- `programa`: Opcional, máximo 100 caracteres

## Ejemplos con cURL

### Login
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan.perez@unipaz.edu.co",
    "contrasena": "password123"
  }'
```

### Register
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María",
    "apellido": "González",
    "correo": "maria.gonzalez@unipaz.edu.co",
    "contrasena": "password123",
    "rol": "ESTUDIANTE",
    "programa": "Ingeniería de Sistemas"
  }'
```

## Códigos de Estado HTTP

- **200 OK**: Operación exitosa
- **400 Bad Request**: Error en los datos enviados (validación fallida)
- **401 Unauthorized**: Credenciales inválidas (solo en login)
- **500 Internal Server Error**: Error interno del servidor

## Mejoras Adicionales Sugeridas

1. **Rate Limiting**: Limitar intentos de login
2. **Logging**: Registrar intentos de autenticación
3. **Refresh Token**: Implementar renovación de tokens
4. **Confirmación de Email**: Verificar correo electrónico
5. **Recuperación de Contraseña**: Endpoint para reset de contraseña
