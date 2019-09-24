package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.Responsible;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsibleRepository extends JpaRepository<Responsible, Integer> {
}
