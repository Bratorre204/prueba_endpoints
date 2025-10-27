package com.asistencia.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sesiones_detalle",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_sesion", "id_estudiante"}))
public class SesionDetalle extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sesion", nullable = false)
    private Sesion sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Usuario estudiante;

    private LocalDateTime fechaFirma;
    private EstadoAsistencia estado;
    private Double latitudEstudiante;
    private Double longitudEstudiante;
    private Double distanciaMetros;
    private String observacion;
}
