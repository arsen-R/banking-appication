package com.arsen.userservice.repository;

import com.arsen.userservice.model.entiry.Role;
import com.arsen.userservice.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
    Boolean existsByRoleName(RoleName roleName);
}
