package com.kverchi.diary.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tipo_objetivo")
@Data
@EqualsAndHashCode(callSuper = true)
public class ObjectiveType extends NamedEntityModel implements Serializable {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_padre_id")
    private ObjectiveType parent;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "tipo_objetivo_tipo_jerarquia",
            joinColumns = {@JoinColumn(name = "tipo_objetivoid")},
            inverseJoinColumns = {@JoinColumn(name = "tipo_jerarquiaid")}
    )
    private List<HierarchyType> hierarchyTypes;

    @Column(name = "state")
    private int state;

    public void addObjective(HierarchyType hierarchyType) {
        if (hierarchyTypes == null) hierarchyTypes = new ArrayList<>();
        hierarchyTypes.add(hierarchyType);
    }
}
