package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipo_jerarquia")
@Data
@EqualsAndHashCode(callSuper = true)
public class HierarchyType extends NamedEntityModel implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipality municipality;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_jerarquia_id")
    private HierarchyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_responsableid")
    private ResponsibleType responsibleType;

    @OneToMany(mappedBy="type")
    private List<Hierarchy> hierarchies;

    @ManyToMany(mappedBy = "hierarchyTypes")
    private List<ObjectiveType> objectiveTypes;

    @Column(name = "state")
    private int state;

    public void addObjective(ObjectiveType objectiveType) {
        if (objectiveTypes == null) objectiveTypes = new ArrayList<>();
        objectiveTypes.add(objectiveType);
    }

}
