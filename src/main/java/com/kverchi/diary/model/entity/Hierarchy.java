package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="jerarquia")
@Data
public class Hierarchy extends NamedEntityModel implements Serializable {

    public static final DateFormat DATE_FORMAT= new SimpleDateFormat("yyyy-mm-dd");

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "periodoid")
    private Period period;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    private User userid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jerarquia_padre_id")
    private Hierarchy parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jerarquia_id")
    private HierarchyType type;

    @ManyToMany(mappedBy = "hierarchies", fetch = FetchType.LAZY)
    private List<Objective> objectives;

    @ManyToMany(mappedBy = "hierarchies", fetch = FetchType.LAZY)
    private List<Responsible> responsibles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hierarchy")
    private List<SemaphoreRange> semaphoreRanges;

    @Column(name = "is_open")
    private boolean open = true;


    public void addObjective(Objective objective){
        if (objectives == null) objectives = new ArrayList<>();
        objectives.add(objective);
    }


}
