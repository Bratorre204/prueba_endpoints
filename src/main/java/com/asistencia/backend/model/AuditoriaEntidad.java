package com.asistencia.backend.model;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditoriaEntidad {
    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @CreatedBy
    @Column(name = "creado_por", updatable = false)
    private String creadoPor;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @LastModifiedBy
    @Column(name = "modificado_por")
    private String modificadoPor;

    @Column(name = "delete_logic")
    private Boolean deleteLogic = false;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "delete_user")
    private String deleteUser;
}
