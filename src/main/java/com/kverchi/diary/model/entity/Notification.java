package com.kverchi.diary.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="notifications")
public class Notification extends NamedEntityModel implements Serializable {

    @Column(name="user_id")
    private int userId;

    @Column(name = "action")
    private String observation;

    @Column(name = "city")
    private int city;

    @Column(name = "register_id")
    private int registerId;

    @Column(name = "table_name")
    private String table;

    @Column(name = "state")
    private int state;

    @Column(name = "created_at")
    private Date created_at;



}
