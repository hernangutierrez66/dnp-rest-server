package com.kverchi.diary.model.entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="rango_semaforo")
@Data
@EqualsAndHashCode(callSuper = true)
public class SemaphoreRange extends NamedEntityModel implements Serializable {

    @Column(name = "color")
    @NotNull(message = "Semaphore needs a color")
    private int color;

    @Column(name = "rango_inicio")
    @NotNull(message = "Semaphore needs a start range")
    private int start;

    @Column(name = "rango_fin")
    @NotNull(message = "Semaphore needs an end range")
    private int end;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "linea_baseid")
    private Hierarchy hierarchy;

    @Column(name = "state")
    private int state;


}
