package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="atributo_valor")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttributeValue extends EntityModel implements Serializable {


    @Column(name = "valor")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atributo_definicion_tipo_objetivoid")
    private AttributeDefinitionObjectiveType attributeDefinitionObjectiveType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objetivoid")
    private  Objective objective;

    @Column(name = "state")
    private int state;
}
