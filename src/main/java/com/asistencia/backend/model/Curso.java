package com.asistencia.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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

    // Relación con la asignatura (materia general)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignatura", nullable = false)
    @JsonBackReference("asignatura-cursos")
    private Asignatura asignatura;

    // Información específica del curso
    private String nombre; // Ej: "Programación I - Diurna"
    private String periodo; // Ej: "2024-1"
    private String turno; // DIURNA, NOCTURNA, etc.
    private String seccion; // Ej: "A", "B", "C"
    private String aula; // Ej: "Aula 101"
    private String horario; // Ej: "Lunes 8:00-10:00"
    
    // Campos adicionales para la estructura del código
    private Integer año; // Ej: 2025
    private Integer semestre; // Ej: 1, 2

    // Relación con el profesor asignado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor", nullable = false)
    @JsonBackReference("profesor-cursos")
    private Usuario profesor;

    // Relación con estudiantes inscritos
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("curso-estudiantes")
    private List<UsuarioCurso> estudiantes;

    // Relación con sesiones
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("curso-sesiones")
    private List<Sesion> sesiones;
}
