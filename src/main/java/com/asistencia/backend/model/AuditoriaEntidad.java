package com.asistencia.backend.model;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @CreatedBy
    @Column(name = "create_user", updatable = false)
    private String createUser;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @LastModifiedBy
    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "delete_logic")
    private Boolean deleteLogic = false;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "delete_user")
    private String deleteUser;
}
