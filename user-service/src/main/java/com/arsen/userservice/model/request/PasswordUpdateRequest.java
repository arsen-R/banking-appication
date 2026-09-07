package com.arsen.userservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public record PasswordUpdateRequest(
        @Size(min = 8, message = "Must be at least 8 characters")
        @NotBlank(message = "This field is required")
        String currentPassword,
        @Size(min = 8, message = "Must be at least 8 characters")
        @NotBlank(message = "This field is required")
        String newPassword
) {
}