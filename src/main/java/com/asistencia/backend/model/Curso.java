package com.asistencia.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="cursos")
public class Curso extends AuditoriaEntidad{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_curso;
    private String nombre_curso;
    private String descripcion_curso;


    // Relación: un curso pertenece a un profesor
    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Usuario profesor;
}
