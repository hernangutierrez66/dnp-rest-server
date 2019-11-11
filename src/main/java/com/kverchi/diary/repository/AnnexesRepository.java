package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.Annexes;

import com.kverchi.diary.model.entity.TraceChangeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnexesRepository extends JpaRepository<Annexes, Integer>  {

    List<Annexes> findByTraceChangeValue(TraceChangeValue traceChangeValue);
}
