package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.Hierarchy;
import com.kverchi.diary.model.entity.SemaphoreRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemaphoreRangeRepository extends JpaRepository<SemaphoreRange, Long> {
    List<SemaphoreRange> findByHierarchy(Hierarchy hierarchy);
}
