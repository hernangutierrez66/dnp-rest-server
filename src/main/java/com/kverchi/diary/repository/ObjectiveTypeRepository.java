package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.ObjectiveType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectiveTypeRepository extends JpaRepository<ObjectiveType, Integer> {
}
