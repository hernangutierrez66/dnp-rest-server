package com.kverchi.diary.model.entity;



import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="atributo_definicion")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttributeDefinition extends NamedEntityModel implements Serializable {

    @Column(name = "descripcion")
    private String description;

    @Column(name = "tipo")
    private int type;

    @Column(name = "state")
    private int state;
}
