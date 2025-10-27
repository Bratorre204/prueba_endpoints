package com.asistencia.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sesiones")
public class Sesion extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el docente (usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor", nullable = false)
    private Usuario profesor;

    // Relación con el curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    // Información de la sesión
    private String nombre;
    private String descripcion;
    private String aula;

    // Coordenadas del profesor
    private Double latitudProfesor;
    private Double longitudProfesor;

    // Radio de proximidad en metros (fijo: 6 metros)
    @Builder.Default
    private Double radioProximidad = 6.0;

    // Fechas de la sesión
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCierre;

    // Estado de la sesión (siempre ACTIVA al crear)
    @Builder.Default
    private String estado = "ACTIVA"; // ACTIVA, FINALIZADA, CANCELADA

    // Relación con detalles de asistencia
    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SesionDetalle> detalles;
}
