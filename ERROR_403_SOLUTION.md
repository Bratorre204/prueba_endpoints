# Solución para Error 403 - Listar Cursos del Profesor

## 🔍 **Diagnóstico del Problema**

El error 403 indica un problema de **autorización**. Aunque la configuración permite acceso sin autenticación a `/api/cursos/**`, puede haber conflictos con el filtro JWT.

## ✅ **Soluciones Propuestas**

### **Solución 1: Incluir Token JWT (Recomendada)**

Si tienes un token JWT válido, inclúyelo en la petición:

```bash
# Con token JWT
curl -X GET "http://localhost:8081/api/cursos/profesor/1" \
  -H "Authorization: Bearer TU_TOKEN_JWT_AQUI"
```

### **Solución 2: Verificar sin Token**

Prueba primero sin token para confirmar que el endpoint funciona:

```bash
# Sin token (debería funcionar según la configuración)
curl -X GET "http://localhost:8081/api/cursos/profesor/1"
```

### **Solución 3: Obtener Token JWT Primero**

Si no tienes token, obtén uno primero:

```bash
# 1. Login para obtener token
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "profesor@unipaz.edu.co",
    "contrasena": "password123"
  }'

# 2. Usar el token en la respuesta para listar cursos
curl -X GET "http://localhost:8081/api/cursos/profesor/1" \
  -H "Authorization: Bearer TOKEN_DE_LA_RESPUESTA_ANTERIOR"
```

## 🔧 **Configuración Temporal para Debugging**

Si el problema persiste, puedes temporalmente deshabilitar la seguridad para este endpoint específico:

### **Opción A: Endpoint Público Temporal**
```java
// En SecurityConfig.java - línea 29
.requestMatchers("/api/cursos/profesor/**").permitAll()
```

### **Opción B: Verificar Logs**
Revisa los logs de la aplicación para ver el error específico:
```bash
# En los logs deberías ver algo como:
# "Access denied for user: anonymous"
# o
# "JWT token validation failed"
```

## 📋 **Pasos para Resolver**

### **1. Verificar el Endpoint**
```bash
# Probar endpoint básico
curl -X GET "http://localhost:8081/api/cursos/profesor/1"
```

### **2. Verificar con Token**
```bash
# Obtener token
curl -X POST "http://localhost:8081/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo": "tu_email", "contrasena": "tu_password"}'

# Usar token
curl -X GET "http://localhost:8081/api/cursos/profesor/1" \
  -H "Authorization: Bearer TOKEN"
```

### **3. Verificar ID del Profesor**
Asegúrate de que el ID del profesor existe en la base de datos:
```bash
# Listar todos los usuarios para ver IDs
curl -X GET "http://localhost:8081/api/usuarios"
```

## 🚨 **Posibles Causas del Error 403**

1. **Token JWT inválido o expirado**
2. **Filtro JWT bloqueando la petición**
3. **ID de profesor inexistente**
4. **Configuración de seguridad conflictiva**
5. **Headers faltantes en la petición**

## 💡 **Recomendación**

**Prueba primero sin token** para confirmar que el endpoint funciona, luego implementa la autenticación JWT si es necesaria.

¿Puedes probar estas soluciones y decirme cuál funciona?
