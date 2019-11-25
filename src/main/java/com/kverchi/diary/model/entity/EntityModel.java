package com.kverchi.diary.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class EntityModel implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;


}
