package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.jdo.annotations.Unique;
import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="atributo_definicion_tipo_objetivo")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttributeDefinitionObjectiveType extends EntityModel implements Serializable {

    @Unique
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atributo_definicionid")
    private AttributeDefinition attributeDefinition;

    @Unique
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_objetivoid")
    private ObjectiveType objectiveType;


    @Column(name = "state")
    private int state;
}
