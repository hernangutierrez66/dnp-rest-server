package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="traza_cambios_valor")
@Data
@EqualsAndHashCode(callSuper = true)
public class TraceChangeValue extends EntityModel implements Serializable {

    public static final DateFormat DATE_FORMAT= new SimpleDateFormat("yyyy-mm-dd");

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objetivoid")
    private Objective objective;


    @Column(name = "valor")
    private int value;

    @Column(name = "fecha_cambio")
    @Temporal(TemporalType.DATE)
    private Date dateChange;

    @Column(name = "state")
    private int state;


}
