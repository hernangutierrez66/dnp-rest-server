package com.kverchi.diary.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="municipio")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"secretaries"})
public class Municipality extends NamedEntityModel implements Serializable {

    @Column(name = "cod_dane")
    @NotNull(message = "Municipality needs a code")
    private Integer daneCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamentoid")
    private Department department;

    @OneToMany(mappedBy = "municipality")
    @JsonIgnore
    private List<Responsible> secretaries;

    @Column(name = "state")
    private int state;



}
