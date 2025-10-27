# Problemas Identificados y Solucionados al Arrancar el Proyecto

## 🔍 **Problemas Encontrados:**

### ❌ **1. Mapeo de Campos Incorrecto**
- **Problema**: Los campos del modelo `Usuario` no tenían anotaciones `@Column` explícitas
- **Síntoma**: Posibles errores de mapeo JPA con la base de datos
- **Solución**: Agregué `@Column(name = "campo")` a todos los campos

### ❌ **2. Inconsistencia en Auditoría**
- **Problema**: `AuditoriaEntidad` usaba nombres en inglés (`createDate`, `createUser`) pero la BD usa español (`fecha_creacion`, `creado_por`)
- **Síntoma**: Errores de mapeo de columnas de auditoría
- **Solución**: Corregí los nombres de columnas para que coincidan con la BD

### ❌ **3. Modelo Rol Incompleto**
- **Problema**: Faltaban anotaciones `@Builder` y `@EqualsAndHashCode`
- **Síntoma**: Posibles errores de construcción de objetos
- **Solución**: Agregué todas las anotaciones necesarias

## ✅ **Correcciones Aplicadas:**

### 1. **Modelo Usuario Corregido**
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_usuario")
private Long id_usuario;

@Column(name = "nombre")
private String nombre;

@Column(name = "apellido")
private String apellido;

@Column(nullable = false, name = "correo")
private String correo;

@Column(name = "contrasena")
private String contrasena;

@Column(name = "programa")
private String programa;
```

### 2. **AuditoriaEntidad Corregida**
```java
@CreatedDate
@Column(name = "fecha_creacion", updatable = false)
private LocalDateTime fechaCreacion;

@CreatedBy
@Column(name = "creado_por", updatable = false)
private String creadoPor;

@LastModifiedDate
@Column(name = "fecha_modificacion")
private LocalDateTime fechaModificacion;

@LastModifiedBy
@Column(name = "modificado_por")
private String modificadoPor;
```

### 3. **Modelo Rol Corregido**
```java
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Rol extends AuditoriaEntidad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long id_rol;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, name = "nombre")
    private RolNombre nombre;
}
```

## 🚀 **Pasos para Arrancar el Proyecto:**

### 1. **Verificar Base de Datos**
```bash
# Asegúrate de que PostgreSQL esté corriendo
# Verifica la conexión a la BD en application.properties
```

### 2. **Ejecutar Script SQL**
```bash
# Ejecuta el script database_schema.sql en tu BD
psql -h 34.55.17.5 -U liderdb -d DesarrolloV1 -f database_schema.sql
```

### 3. **Arrancar la Aplicación**
```bash
./gradlew bootRun
# o
./gradlew build && java -jar build/libs/asistencia-0.0.1-SNAPSHOT.jar
```

### 4. **Verificar que Funciona**
```bash
# Probar endpoint de salud
curl http://localhost:8081/auth/login

# Debería devolver un error 400 (Bad Request) por falta de datos
# Esto indica que la aplicación está funcionando
```

## 🔧 **Configuración Adicional Recomendada:**

### 1. **Agregar Endpoint de Salud**
```java
@RestController
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }
}
```

### 2. **Configurar Logging**
```properties
# En application.properties
logging.level.com.asistencia.backend=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### 3. **Configurar Puerto Alternativo**
```properties
# En application.properties
server.port=8081
# Si el puerto está ocupado, cambia a otro
```

## 🐛 **Posibles Errores y Soluciones:**

### Error: "Table 'usuarios' doesn't exist"
**Solución**: Ejecuta el script `database_schema.sql`

### Error: "Connection refused"
**Solución**: Verifica que PostgreSQL esté corriendo y la conexión en `application.properties`

### Error: "Port 8081 already in use"
**Solución**: Cambia el puerto en `application.properties` o mata el proceso que usa el puerto

### Error: "Bean 'auditorAware' not found"
**Solución**: Verifica que `AuditorAwareImpl` esté anotado con `@Component("auditorAware")`

## ✅ **Verificación Final:**

Si todo está correcto, deberías ver en la consola:
```
Started AsistenciaApplication in X.XXX seconds (JVM running for X.XXX)
```

Y poder acceder a:
- `http://localhost:8081/auth/login` (POST)
- `http://localhost:8081/auth/register` (POST)
- `http://localhost:8081/api/asignaturas` (GET)
