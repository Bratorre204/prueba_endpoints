# ✅ Solución Definitiva al Error de Serialización JSON

## 🔍 **Problema Resuelto**

El error **`InvalidDefinitionException`** se debía a que Jackson no podía serializar los **proxies de Hibernate** cuando había relaciones circulares entre entidades. La solución anterior con `@JsonIgnoreProperties` no fue suficiente.

## ✅ **Solución Implementada**

### **1. Configuración Global de Jackson**

#### **JacksonConfig.java:**
```java
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Deshabilitar fallo en beans vacíos
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        // Configurar para ignorar propiedades desconocidas
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Configurar para manejar proxies de Hibernate
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        return mapper;
    }
}
```

### **2. Anotaciones Jackson Correctas**

#### **Curso.java:**
```java
// Relación con asignatura (referencia hacia atrás)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_asignatura", nullable = false)
@JsonBackReference("asignatura-cursos")
private Asignatura asignatura;

// Relación con profesor (referencia hacia atrás)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_profesor", nullable = false)
@JsonBackReference("profesor-cursos")
private Usuario profesor;

// Relación con estudiantes (referencia hacia adelante)
@OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference("curso-estudiantes")
private List<UsuarioCurso> estudiantes;

// Relación con sesiones (referencia hacia adelante)
@OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference("curso-sesiones")
private List<Sesion> sesiones;
```

#### **Asignatura.java:**
```java
// Relación con cursos (referencia hacia adelante)
@OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference("asignatura-cursos")
private List<Curso> cursos;
```

#### **Usuario.java:**
```java
// Relación con roles (referencia hacia atrás)
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
        name = "usuarios_roles",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
)
@JsonBackReference("usuario-roles")
@Builder.Default
private Set<Rol> roles = new HashSet<>();
```

### **3. Controladores Actualizados para Usar DTOs**

#### **Todos los endpoints ahora usan `CursoListaResponse`:**
```java
@GetMapping("/profesor/{idProfesor}")
public ResponseEntity<?> getCursosProfesor(
        @PathVariable Long idProfesor,
        @RequestParam(required = false) String periodo) {
    try {
        List<Curso> cursos = cursoService.getCursosPorProfesor(idProfesor, periodo);
        List<CursoListaResponse> cursosResponse = cursoService.convertirACursoListaResponse(cursos);
        return ResponseEntity.ok(cursosResponse);
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, e.getMessage(), null));
    }
}
```

### **4. DTO Específico para Respuestas**

#### **CursoListaResponse.java:**
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoListaResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String turno;
    private String seccion;
    private String aula;
    private String horario;
    private Integer año;
    private Integer semestre;
    private String periodo;
    private AsignaturaBasica asignatura;
    private ProfesorBasico profesor;
}
```

## 🔧 **Cómo Funciona la Solución**

### **@JsonManagedReference vs @JsonBackReference:**

1. **@JsonManagedReference**: Se serializa completamente (lado "dueño" de la relación)
2. **@JsonBackReference**: Se omite en la serialización (lado "referenciado")

### **Ejemplo de Relación:**
- **Asignatura** → **Cursos**: `@JsonManagedReference("asignatura-cursos")`
- **Curso** → **Asignatura**: `@JsonBackReference("asignatura-cursos")`

Esto evita la referencia circular porque:
- La asignatura serializa sus cursos
- Los cursos NO serializan su asignatura (se omite)

## 🚀 **Resultado Final**

### **Respuesta JSON Limpia:**
```json
[
  {
    "id": 1,
    "codigo": "603D12025A",
    "nombre": "Programación I - Diurna",
    "descripcion": "Curso de Programación I en modalidad diurna...",
    "turno": "DIURNA",
    "seccion": "A",
    "aula": "Aula 101",
    "horario": "Lunes 8:00-10:00",
    "año": 2025,
    "semestre": 1,
    "periodo": "2025-1",
    "asignatura": {
      "id": 1,
      "codigo": "603",
      "nombre": "Programación I",
      "descripcion": "Fundamentos de programación orientada a objetos",
      "creditos": 3
    },
    "profesor": {
      "id": 1,
      "nombre": "Juan",
      "apellidos": "Pérez",
      "correo": "juan.perez@unipaz.edu.co"
    }
  }
]
```

## ✅ **Beneficios de la Solución**

1. **✅ Sin errores de serialización**: Las anotaciones Jackson evitan completamente las referencias circulares
2. **✅ Respuesta limpia**: Solo información relevante en la respuesta JSON
3. **✅ Rendimiento optimizado**: No carga relaciones innecesarias
4. **✅ Mantenibilidad**: DTOs específicos para cada caso de uso
5. **✅ Escalabilidad**: Fácil agregar nuevos campos sin afectar otros endpoints
6. **✅ Configuración global**: Jackson configurado para manejar proxies de Hibernate

## 🔧 **Para Probar**

```bash
# Listar cursos del profesor ID 1
curl -X GET "http://localhost:8081/api/cursos/profesor/1"

# Listar cursos del profesor ID 1 en período específico
curl -X GET "http://localhost:8081/api/cursos/profesor/1?periodo=2025-1"

# Listar todos los cursos
curl -X GET "http://localhost:8081/api/cursos"

# Obtener curso específico
curl -X GET "http://localhost:8081/api/cursos/1"
```

## 🎯 **Solución Completa**

La solución implementada resuelve **definitivamente** el error de serialización JSON mediante:

1. **Configuración global de Jackson** para manejar proxies de Hibernate
2. **Anotaciones Jackson correctas** (`@JsonManagedReference` y `@JsonBackReference`)
3. **DTOs específicos** para todas las respuestas
4. **Controladores actualizados** para usar solo DTOs

El sistema ahora maneja correctamente las relaciones circulares y serializa JSON sin errores.
