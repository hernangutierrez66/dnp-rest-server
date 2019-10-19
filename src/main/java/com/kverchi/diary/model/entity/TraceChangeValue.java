package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
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
    private ZonedDateTime dateChange;

    @Column(name = "state")
    private int state;

    @Column(name = "created_at")
    @CreatedDate
    private Date created_at;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updated_at;

    public TraceChangeValue(Objective objective, int value, ZonedDateTime dateChange, int state, ZonedDateTime created_at, ZonedDateTime updated_at) {
        this.objective = objective;
        this.value = value;
        this.dateChange = dateChange;
        this.state = state;
        //this.created_at = created_at;
        //this.updated_at = updated_at;
    }
}
