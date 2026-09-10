package com.arsen.userservice.repository;

import com.arsen.userservice.model.entity.Role;
import com.arsen.userservice.model.enums.RoleName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void testFindByRoleNameShouldReturnRole() {
        Role adminRole = new Role(RoleName.ADMIN);
        roleRepository.save(adminRole);

        Optional<Role> foundRole = roleRepository.findByRoleName(adminRole.getRoleName());

        assertNotNull(foundRole);
        assertTrue(foundRole.isPresent());
        assertEquals(adminRole.getRoleName(), foundRole.get().getRoleName());
    }

    @Test
    void testFindByRoleNameShouldReturnNullWhenNotFound() {
        Optional<Role> foundRole = roleRepository.findByRoleName(RoleName.MODERATOR);

        assertTrue(foundRole.isEmpty());
    }

    @Test
    void testExistsByRoleNameShouldReturnTrue() {
        Role adminRole = new Role(RoleName.CUSTOMER);
        roleRepository.save(adminRole);

        Boolean existsByRoleName = roleRepository.existsByRoleName(adminRole.getRoleName());

        assertTrue(existsByRoleName);
    }

    @Test
    void testExistsByRoleNameShouldReturnFalseWhenNotExists() {
        Boolean existsByRoleName = roleRepository.existsByRoleName(RoleName.EMPLOYEE);
        assertFalse(existsByRoleName);
    }
}