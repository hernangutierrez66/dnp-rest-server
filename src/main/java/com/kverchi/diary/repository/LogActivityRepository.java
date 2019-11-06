package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.LogActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogActivityRepository extends JpaRepository<LogActivity, Integer> {
}
