package com.kverchi.diary.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "responsable")
@Data
@EqualsAndHashCode(callSuper = true)
public class Responsible extends NamedEntityModel implements Serializable {

    @Column(name = "nit")
    private Integer nit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_responsableid")
    private ResponsibleType responsibleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsableid")
    private Responsible parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipioid")
    private Municipality municipality;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "jerarquia_responsable",
            joinColumns = {@JoinColumn(name = "responsableid")},
            inverseJoinColumns = {@JoinColumn(name = "linea_baseid")}
    )
    private List<Hierarchy> hierarchies;

    @Column(name = "state")
    private int state;


}
