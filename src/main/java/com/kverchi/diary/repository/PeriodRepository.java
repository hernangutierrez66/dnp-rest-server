package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.Period;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository extends JpaRepository<Period, Integer> {
}
