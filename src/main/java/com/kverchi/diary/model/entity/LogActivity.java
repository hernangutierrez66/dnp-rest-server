package com.kverchi.diary.model.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Data
@Entity
@Table(name="log_activities")
public class LogActivity extends NamedEntityModel implements Serializable {

    @Column(name="user_id")
    private int userId;

    @Column(name = "action")
    private String accion;

    @Column(name = "city")
    private int ciudad;

    @Column(name = "created_at")
    private ZonedDateTime created_at;

    public LogActivity(String name, int userId, String accion, int ciudad, ZonedDateTime created_at) {
        this.name = name;
        this.userId = userId;
        this.accion = accion;
        this.ciudad = ciudad;
        this.created_at = created_at;
    }
}
