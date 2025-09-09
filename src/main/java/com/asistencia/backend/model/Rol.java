package com.asistencia.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// entity/Rol.java
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol extends AuditoriaEntidad{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_rol;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RolNombre nombre;

    public Rol(RolNombre nombre) {
        this.nombre = nombre;
    }
}