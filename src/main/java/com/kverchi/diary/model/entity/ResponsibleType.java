package com.kverchi.diary.model.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="tipo_responsable")

@EqualsAndHashCode(callSuper = true)
public class ResponsibleType extends NamedEntityModel implements Serializable {

    @Column(name = "requiere_municipio")
    private boolean requiresMunicipality;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_responsableid")
    @NotNull(message = "Responsable type needs a parent")
    private ResponsibleType parent;

    @Column(name = "state")
    private int state;

    public boolean isRequiresMunicipality() {
        return requiresMunicipality;
    }

    public void setRequiresMunicipality(boolean requiresMunicipality) {
        this.requiresMunicipality = requiresMunicipality;
    }
}
