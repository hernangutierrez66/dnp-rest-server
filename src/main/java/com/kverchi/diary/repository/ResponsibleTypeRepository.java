package com.kverchi.diary.repository;


import com.kverchi.diary.model.entity.ResponsibleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsibleTypeRepository extends JpaRepository<ResponsibleType, Integer> {


}
