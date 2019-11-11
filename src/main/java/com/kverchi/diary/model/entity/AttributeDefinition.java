package com.kverchi.diary.model.entity;



import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public AttributeDefinition() {
        super();
    }

    public AttributeDefinition(String name, String description, int type, int state) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.state = state;
    }
}
