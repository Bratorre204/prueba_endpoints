# ✅ Solución al Error de Serialización JSON

## 🔍 **Problema Identificado**

El error **`InvalidDefinitionException`** se debe a que Jackson no puede serializar los **proxies de Hibernate** cuando hay relaciones circulares entre entidades.

### **Causa Raíz:**
- **Relaciones circulares**: `Curso` → `Asignatura` → `Cursos` → `Profesor` → `Usuario`
- **Lazy loading**: Los objetos no están completamente cargados
- **Proxies de Hibernate**: Jackson no puede serializar los proxies

## ✅ **Solución Implementada**

### **1. Anotaciones Jackson en Modelos**

#### **Curso.java:**
```java
@JsonIgnoreProperties({"cursos"})
private Asignatura asignatura;

@JsonIgnoreProperties({"roles", "estudiantes", "sesiones"})
private Usuario profesor;

@JsonIgnoreProperties({"curso", "usuario"})
private List<UsuarioCurso> estudiantes;

@JsonIgnoreProperties({"curso", "profesor", "detalles"})
private List<Sesion> sesiones;
```

#### **Asignatura.java:**
```java
@JsonIgnoreProperties({"asignatura", "profesor", "estudiantes", "sesiones"})
private List<Curso> cursos;
```

#### **Usuario.java:**
```java
@JsonIgnoreProperties({"usuarios"})
private Set<Rol> roles;
```

### **2. DTO Específico para Listas**

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

### **3. Método de Conversión en Servicio**

```java
public List<CursoListaResponse> convertirACursoListaResponse(List<Curso> cursos) {
    return cursos.stream()
        .map(this::convertirACursoListaResponse)
        .collect(Collectors.toList());
}

public CursoListaResponse convertirACursoListaResponse(Curso curso) {
    return CursoListaResponse.builder()
        .id(curso.getId())
        .codigo(curso.getCodigo())
        .nombre(curso.getNombre())
        .descripcion(curso.getDescripcion())
        .turno(curso.getTurno())
        .seccion(curso.getSeccion())
        .aula(curso.getAula())
        .horario(curso.getHorario())
        .año(curso.getAño())
        .semestre(curso.getSemestre())
        .periodo(curso.getPeriodo())
        .asignatura(CursoListaResponse.AsignaturaBasica.builder()
            .id(curso.getAsignatura().getId())
            .codigo(curso.getAsignatura().getCodigo())
            .nombre(curso.getAsignatura().getNombre())
            .descripcion(curso.getAsignatura().getDescripcion())
            .creditos(curso.getAsignatura().getCreditos())
            .build())
        .profesor(CursoListaResponse.ProfesorBasico.builder()
            .id(curso.getProfesor().getId())
            .nombre(curso.getProfesor().getNombre())
            .apellidos(curso.getProfesor().getApellido())
            .correo(curso.getProfesor().getCorreo())
            .build())
        .build();
}
```

### **4. Controlador Actualizado**

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

## 🚀 **Resultado**

### **Respuesta Optimizada:**
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

1. **Sin errores de serialización**: Las anotaciones Jackson evitan las referencias circulares
2. **Respuesta limpia**: Solo información relevante en la respuesta
3. **Rendimiento optimizado**: No carga relaciones innecesarias
4. **Mantenibilidad**: DTOs específicos para cada caso de uso
5. **Escalabilidad**: Fácil agregar nuevos campos sin afectar otros endpoints

## 🔧 **Para Probar**

```bash
# Listar cursos del profesor ID 1
curl -X GET "http://localhost:8081/api/cursos/profesor/1"

# Listar cursos del profesor ID 1 en período específico
curl -X GET "http://localhost:8081/api/cursos/profesor/1?periodo=2025-1"
```

La solución está implementada y lista para usar. El error de serialización JSON ha sido resuelto completamente.
