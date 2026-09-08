package com.arsen.userservice.model.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record UserProfileDto(
    String firstName,
    String middleName,
    String lastName,
    Date birthday,
    String cellPhoneNumber
) {
    public UserProfileDto(String firstName, String lastName, Date birthday, String cellPhoneNumber) {
        this(firstName, "", lastName, birthday, cellPhoneNumber);
    }
}
