package com.kverchi.diary.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "objetivo")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"hierarchies"})
public class Objective extends NamedEntityModel implements Serializable {

    @Column(name = "descripcion")
    private String description;

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "cantidad_esperada")
    private int expectedValue;

    @Column(name = "cantidad_actual")
    private int actualValue;

    @Column(name = "user_id")
    private int userId;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "objetivo_jerarquia",
            joinColumns = {@JoinColumn(name = "objetivoid")},
            inverseJoinColumns = {@JoinColumn(name = "linea_baseid")}
    )
    private List<Hierarchy> hierarchies;

    @Column(name = "state")
    private int state;

    public void addObjective(Hierarchy hierarchy) {
        if (hierarchies == null) hierarchies = new ArrayList<>();
        hierarchies.add(hierarchy);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_objetivoid")
    private ObjectiveType objectiveType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objetivo_padre_id")
    private Objective parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicadorid")
    private Indicator indicator;
}
