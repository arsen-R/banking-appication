package com.arsen.userservice.model.dto;

import com.arsen.userservice.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Builder;

import java.util.HashSet;
import java.util.Set;

public record UserDto(
        String id,
        String username,
        String email,
        UserStatus userStatus,
        UserProfileDto userProfile,
        Set<RoleDto> roles
) {
    public UserDto(String username, String email, UserStatus userStatus) {
        this(null, username, email, userStatus, null, Set.of());
    }

    public UserDto(String username, String email, UserStatus userStatus, UserProfileDto userProfile, Set<RoleDto> roles) {
        this(null, username, email, userStatus, userProfile, roles);
    }
}
