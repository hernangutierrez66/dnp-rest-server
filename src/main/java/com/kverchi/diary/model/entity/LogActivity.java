package com.kverchi.diary.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

import java.util.Date;

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
    private Date created_at;

    public LogActivity(int userId, String accion, int ciudad) {
        this.userId = userId;
        this.accion = accion;
        this.ciudad = ciudad;
    }
}
