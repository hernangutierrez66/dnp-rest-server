package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "periodo")
@Data
@EqualsAndHashCode(callSuper = true)
public class Period extends NamedEntityModel implements Serializable {

    @Column(name = "duracion")
    private Integer duration;

    @Column(name = "state")
    private int state;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
