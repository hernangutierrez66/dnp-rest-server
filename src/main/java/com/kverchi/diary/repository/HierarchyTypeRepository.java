package com.kverchi.diary.repository;

import com.kverchi.diary.model.entity.HierarchyType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HierarchyTypeRepository extends JpaRepository<HierarchyType, Integer> {

    //List<Hierarchy> findByType(HierarchyType hierarchyType);

    //List<Hierarchy> findByParent(HierarchyType hierarchyType);
}
