package com.kverchi.diary.repository;


import com.kverchi.diary.model.entity.Hierarchy;
import com.kverchi.diary.model.entity.HierarchyType;
import com.kverchi.diary.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(String name);

    List<Role> findByRoleIsNotLikeAndRoleIsNotLike(String name, String name2);
}
