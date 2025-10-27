package com.asistencia.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, name = "nombre")
    private RolNombre nombre;

    public Rol(RolNombre nombre) {
        this.nombre = nombre;
    }
}