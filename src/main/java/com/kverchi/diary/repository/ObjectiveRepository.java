package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.Objective;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Integer> {
}
