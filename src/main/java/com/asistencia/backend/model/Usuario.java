package com.asistencia.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="usuarios", uniqueConstraints = {@UniqueConstraint(columnNames = {"correo"})})
public class Usuario extends AuditoriaEntidad implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "apellido")
    private String apellido;

    @Column(nullable = false, name = "correo")
    private String correo;

    @Column(name = "contrasena")
    private String contrasena;
    
    @Column(name = "programa")
    private String programa;

    @Column(name = "identificacion", unique = true, nullable = false, length = 20)
    private String identificacion; // Cédula colombiana

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    @JsonBackReference("usuario-roles")
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getNombre()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return getCorreo();
    }

    @Override
    public String getPassword() {
        return getContrasena();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
