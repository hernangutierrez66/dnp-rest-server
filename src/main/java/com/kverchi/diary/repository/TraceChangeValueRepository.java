package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.Annexes;
import com.kverchi.diary.model.entity.Objective;
import com.kverchi.diary.model.entity.TraceChangeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraceChangeValueRepository extends JpaRepository<TraceChangeValue, Integer> {
    List<TraceChangeValue> findByObjective(Objective objective);
}
