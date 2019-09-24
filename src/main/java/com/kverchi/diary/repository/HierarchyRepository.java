package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.Hierarchy;
import com.kverchi.diary.model.entity.HierarchyType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HierarchyRepository extends JpaRepository<Hierarchy, Integer> {

    List<Hierarchy> findByType(HierarchyType hierarchyType);

    List<Hierarchy> findByParent(Hierarchy hierarchy);


}
