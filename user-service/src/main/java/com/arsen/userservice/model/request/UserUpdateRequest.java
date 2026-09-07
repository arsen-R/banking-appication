package com.arsen.userservice.model.request;

import com.arsen.userservice.model.entiry.Role;
import com.arsen.userservice.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Date;
import java.util.Set;

public record UserUpdateRequest(
        @NotBlank(message = "This username is required")
        String username,
        @NotBlank(message = "This email is required")
        @Email(message = "Email is invalid")
        String email,
        @NotBlank(message = "The first name is required")
        String firstName,
        String middleName,
        @NotBlank(message = "The last name is required")
        String lastName,
        @NotNull(message = "The birthday is required")
        Date birthday,
        @NotBlank(message = "The cell phone number is required")
        String cellPhoneNumber
) {
}
