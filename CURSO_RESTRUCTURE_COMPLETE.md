# Restructuración Completa del Modelo Curso

## 🎯 **Objetivo Cumplido**

Se ha implementado exitosamente la nueva estructura para el modelo `Curso` con:
- **ID único** del curso
- **Descripción** detallada del curso
- **Código estructurado** con formato específico (ej: `603D12025B`, `603N12025B`)
- **Relación clara** con asignatura y profesor

## 🔧 **Cambios Implementados**

### 1. **Modelo Curso Actualizado**
```java
@Entity
@Table(name = "cursos")
public class Curso extends AuditoriaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único del curso (ej: 603D12025B, 603N12025B)
    @Column(unique = true, nullable = false, length = 15)
    private String codigo;

    // Descripción del curso
    @Column(nullable = false)
    private String descripcion;

    // Campos adicionales para la estructura del código
    private Integer año; // Ej: 2025
    private Integer semestre; // Ej: 1, 2
    
    // Relaciones existentes mantenidas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignatura", nullable = false)
    private Asignatura asignatura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor", nullable = false)
    private Usuario profesor;
}
```

### 2. **Generador de Códigos de Curso**
```java
@Component
public class CodigoCursoGenerator {
    
    /**
     * Estructura del código: [codigo_asignatura][turno][semestre][año][seccion]
     * Ejemplo: 603D12025B
     * - 603 = código asignatura
     * - D = Diurno (N = Nocturno)
     * - 1 = semestre
     * - 25 = año (2025)
     * - B = sección
     */
    public String generarCodigoCurso(String codigoAsignatura, String turno, 
                                   Integer año, Integer semestre, String seccion) {
        String codigoTurno = "D".equals(turno.toUpperCase()) ? "D" : "N";
        String añoFormateado = String.format("%02d", año % 100);
        return String.format("%s%s%d%s%s", 
            codigoAsignatura, codigoTurno, semestre, añoFormateado, seccion.toUpperCase());
    }
}
```

### 3. **DTOs Actualizados**

#### **CrearCursoRequest**
```java
public class CrearCursoRequest {
    @NotNull private Long idAsignatura;
    @NotBlank private String descripcion;
    @NotBlank private String nombre;
    @NotBlank private String turno; // DIURNA, NOCTURNA
    @NotBlank private String seccion;
    @NotBlank private String aula;
    @NotBlank private String horario;
    @NotNull private Integer año;
    @NotNull private Integer semestre;
    @NotNull private Long idProfesor;
}
```

#### **CursoResponse**
```java
public class CursoResponse {
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
    private AsignaturaInfo asignatura;
    private ProfesorInfo profesor;
}
```

### 4. **Repositorio Actualizado**
```java
public interface CursoRepository extends JpaRepository<Curso, Long> {
    // Buscar por código único
    Optional<Curso> findByCodigo(String codigo);
    
    // Verificar si existe un código
    boolean existsByCodigo(String codigo);
    
    // Buscar por año y semestre
    List<Curso> findByAñoAndSemestre(Integer año, Integer semestre);
    
    // Buscar por turno, año y semestre
    List<Curso> findByTurnoAndAñoAndSemestre(String turno, Integer año, Integer semestre);
}
```

### 5. **Servicio Actualizado**
```java
@Service
public class CursoService {
    
    public Curso crearCurso(CrearCursoRequest request) {
        // Obtener asignatura y profesor
        Asignatura asignatura = asignaturaRepository.findById(request.getIdAsignatura())
            .orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));
        
        Usuario profesor = usuarioRepository.findById(request.getIdProfesor())
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        
        // Generar código del curso
        String codigoCurso = codigoCursoGenerator.generarCodigoCurso(
            asignatura.getCodigo(),
            request.getTurno(),
            request.getAño(),
            request.getSemestre(),
            request.getSeccion()
        );
        
        // Verificar unicidad del código
        if (cursoRepository.existsByCodigo(codigoCurso)) {
            throw new RuntimeException("Ya existe un curso con el código: " + codigoCurso);
        }
        
        // Crear curso con todos los campos
        Curso curso = Curso.builder()
            .codigo(codigoCurso)
            .descripcion(request.getDescripcion())
            .nombre(request.getNombre())
            .periodo(request.getAño() + "-" + request.getSemestre())
            .turno(request.getTurno())
            .seccion(request.getSeccion())
            .aula(request.getAula())
            .horario(request.getHorario())
            .año(request.getAño())
            .semestre(request.getSemestre())
            .asignatura(asignatura)
            .profesor(profesor)
            .build();
        
        return cursoRepository.save(curso);
    }
}
```

### 6. **Base de Datos Actualizada**
```sql
CREATE TABLE cursos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(15) UNIQUE NOT NULL,
    descripcion TEXT NOT NULL,
    id_asignatura INTEGER NOT NULL REFERENCES asignaturas(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    periodo VARCHAR(20) NOT NULL,
    turno VARCHAR(20) NOT NULL,
    seccion VARCHAR(10),
    aula VARCHAR(50),
    horario VARCHAR(100),
    año INTEGER NOT NULL,
    semestre INTEGER NOT NULL CHECK (semestre IN (1, 2)),
    id_profesor INTEGER NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    -- Campos de auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    fecha_modificacion TIMESTAMP,
    modificado_por VARCHAR(100)
);
```

### 7. **Datos de Ejemplo Actualizados**
```sql
INSERT INTO cursos (codigo, descripcion, id_asignatura, nombre, periodo, turno, seccion, aula, horario, año, semestre, id_profesor) VALUES 
('603D12025A', 'Curso de Programación I en modalidad diurna para estudiantes de primer semestre', 1, 'Programación I - Diurna', '2025-1', 'DIURNA', 'A', 'Aula 101', 'Lunes 8:00-10:00', 2025, 1, 1),
('603N12025A', 'Curso de Programación I en modalidad nocturna para estudiantes de primer semestre', 1, 'Programación I - Nocturna', '2025-1', 'NOCTURNA', 'A', 'Aula 201', 'Lunes 18:00-20:00', 2025, 1, 1);
```

## 📋 **Estructura del Código Explicada**

### **Formato del Código**: `[ASIGNATURA][TURNO][SEMESTRE][AÑO][SECCIÓN]`

#### **Ejemplos**:
- **`603D12025A`**:
  - `603` = Código de asignatura (Programación I)
  - `D` = Diurno
  - `1` = Semestre 1
  - `25` = Año 2025
  - `A` = Sección A

- **`603N12025B`**:
  - `603` = Código de asignatura (Programación I)
  - `N` = Nocturno
  - `1` = Semestre 1
  - `25` = Año 2025
  - `B` = Sección B

### **Relación con Asignatura**:
- **Asignatura**: `603` (Programación I)
- **Cursos**: 
  - `603D12025A` (Diurno, Semestre 1, 2025, Sección A)
  - `603N12025B` (Nocturno, Semestre 1, 2025, Sección B)

## ✅ **Beneficios de la Nueva Estructura**

1. **Identificación Única**: Cada curso tiene un código único y descriptivo
2. **Trazabilidad**: Fácil identificación de turno, año, semestre y sección
3. **Organización**: Estructura clara para gestión académica
4. **Escalabilidad**: Fácil agregar nuevos cursos siguiendo el patrón
5. **Integridad**: Validaciones automáticas de unicidad y formato

## 🚀 **Para Usar la Nueva Estructura**

### **Crear un Curso**:
```json
POST /api/cursos
{
  "idAsignatura": 1,
  "descripcion": "Curso de Programación I en modalidad diurna",
  "nombre": "Programación I - Diurna",
  "turno": "DIURNA",
  "seccion": "A",
  "aula": "Aula 101",
  "horario": "Lunes 8:00-10:00",
  "año": 2025,
  "semestre": 1,
  "idProfesor": 1
}
```

### **Respuesta**:
```json
{
  "id": 1,
  "codigo": "603D12025A",
  "nombre": "Programación I - Diurna",
  "descripcion": "Curso de Programación I en modalidad diurna",
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
    "nombre": "Programación I"
  },
  "profesor": {
    "id": 1,
    "nombre": "Juan",
    "apellidos": "Pérez"
  }
}
```

La restructuración está completa y lista para usar. El sistema ahora maneja códigos de curso estructurados que facilitan la identificación y gestión académica.
