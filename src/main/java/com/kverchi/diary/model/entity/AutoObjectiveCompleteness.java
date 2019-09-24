package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="completitud_objetivo_auto")
@Data
@EqualsAndHashCode(callSuper = true)
public class AutoObjectiveCompleteness extends EntityModel implements Serializable {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objetivo_padre_id")
    private Objective objectiveParentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objetivo_hijo_id")
    private Objective objectiveChildId;

    @Column(name = "peso_ponderado")
    private double weightedWeight;

    @Column(name = "state")
    private int state;
}
