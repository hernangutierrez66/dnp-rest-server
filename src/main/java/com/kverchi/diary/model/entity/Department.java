package com.kverchi.diary.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="departamento")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"municipalities"})
public class Department extends NamedEntityModel implements Serializable {

    @Column(name = "cod_dane")
    @NotNull(message = "Department needs a code")
    private Integer daneCode;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Municipality> municipalities;

    @Column(name = "state")
    private int state;
}
