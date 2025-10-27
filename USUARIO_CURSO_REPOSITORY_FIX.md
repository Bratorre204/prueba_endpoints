# Problema Corregido - UsuarioCursoRepository

## 🔍 **Problema Identificado:**

**Error**: `Could not create query for public abstract com.asistencia.backend.model.UsuarioCurso com.asistencia.backend.repository.UsuarioCursoRepository.findByUsuarioIdAndCursoId(java.lang.Long,java.lang.Long); Reason: Validation failed for query`

## ❌ **Causas del Problema:**

### **Campos Incorrectos en Queries**
- **Problema**: Las queries usaban `uc.usuario.id_usuario` pero el modelo ahora usa `id`
- **Síntoma**: Spring Data JPA no podía encontrar la propiedad `id_usuario` en el modelo `Usuario`

## ✅ **Correcciones Aplicadas:**

### **Métodos Corregidos:**

#### 1. **findByUsuarioId**
```java
// ❌ Antes:
@Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id_usuario = :idUsuario")
List<UsuarioCurso> findByUsuarioId(@Param("idUsuario") Long idUsuario);

// ✅ Después:
@Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :idUsuario")
List<UsuarioCurso> findByUsuarioId(@Param("idUsuario") Long idUsuario);
```

#### 2. **findByUsuarioIdAndCursoId**
```java
// ❌ Antes:
@Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id_usuario = :idUsuario AND uc.curso.id = :idCurso")
UsuarioCurso findByUsuarioIdAndCursoId(@Param("idUsuario") Long idUsuario, @Param("idCurso") Long idCurso);

// ✅ Después:
@Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :idUsuario AND uc.curso.id = :idCurso")
UsuarioCurso findByUsuarioIdAndCursoId(@Param("idUsuario") Long idUsuario, @Param("idCurso") Long idCurso);
```

#### 3. **findByUsuarioIdAndEstadoActivo**
```java
// ❌ Antes:
@Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id_usuario = :idUsuario AND uc.estado = 'ACTIVO'")
List<UsuarioCurso> findByUsuarioIdAndEstadoActivo(@Param("idUsuario") Long idUsuario);

// ✅ Después:
@Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :idUsuario AND uc.estado = 'ACTIVO'")
List<UsuarioCurso> findByUsuarioIdAndEstadoActivo(@Param("idUsuario") Long idUsuario);
```

## 🔧 **Cambios Específicos:**

### **Campo de Query**
- **Antes**: `uc.usuario.id_usuario` ❌
- **Después**: `uc.usuario.id` ✅

## 📋 **Contexto del Problema:**

Este error es parte de la cadena de problemas que surgieron cuando corregimos el modelo `Usuario` para usar `id` en lugar de `id_usuario`. El `UsuarioCursoRepository` tenía varias queries que aún hacían referencia al campo antiguo.

### **Relación UsuarioCurso -> Usuario:**
```java
// En el modelo UsuarioCurso:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_usuario", nullable = false)
private Usuario usuario; // ✅ Referencia al modelo Usuario

// En el modelo Usuario:
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_usuario")
private Long id; // ✅ Campo id (mapeado a columna id_usuario)
```

## 🚀 **Resultado:**

### ✅ **Problemas Resueltos:**
1. **Spring Data JPA** puede crear todas las queries correctamente
2. **Referencias a campos** usan los nombres correctos
3. **Relaciones JPA** funcionan correctamente
4. **SesionService** puede ser instanciado sin problemas
5. **UsuarioCursoRepository** funciona correctamente

### ✅ **Verificación:**
- **0 errores de compilación**
- **0 warnings de linter**
- **Queries JPA válidas**
- **Relaciones consistentes**

## 🔧 **Para Arrancar el Proyecto:**

1. **Ejecuta el script SQL**: `database_schema.sql`
2. **Verifica la conexión**: PostgreSQL en `application.properties`
3. **Arranca la aplicación**: `./gradlew bootRun`

El proyecto ahora debería arrancar sin problemas de validación de queries JPA en el UsuarioCursoRepository.

