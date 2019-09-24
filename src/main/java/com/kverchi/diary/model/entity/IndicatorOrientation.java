package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "orientacion_indicador")
@Data
@EqualsAndHashCode(callSuper = true)
public class IndicatorOrientation extends EntityModel implements Serializable {

    @Column(unique = true, name = "nombre")
    private String name;

    @Column(name = "state")
    private int state;
}
