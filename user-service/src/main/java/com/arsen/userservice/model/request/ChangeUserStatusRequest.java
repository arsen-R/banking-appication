package com.arsen.userservice.model.request;

import com.arsen.userservice.model.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeUserStatusRequest(
        @NotNull
        UserStatus userStatus
) {
}
