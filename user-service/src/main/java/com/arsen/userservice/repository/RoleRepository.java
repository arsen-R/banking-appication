package com.arsen.userservice.repository;

import com.arsen.userservice.model.entity.Role;
import com.arsen.userservice.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRoleName(RoleName roleName);
    Boolean existsByRoleName(RoleName roleName);
}
