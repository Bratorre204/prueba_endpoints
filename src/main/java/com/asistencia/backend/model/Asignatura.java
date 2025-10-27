package com.asistencia.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "asignaturas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Asignatura extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false)
    private String nombre; // Ej: "Programación I"

    @Column(nullable = false)
    private String descripcion;

    @Column(length = 10)
    private String abreviatura;

    private Integer creditos;

    // Relación con cursos (instancias específicas)
    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("asignatura-cursos")
    private List<Curso> cursos;
}