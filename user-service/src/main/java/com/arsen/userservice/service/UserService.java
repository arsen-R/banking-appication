package com.arsen.userservice.service;

import com.arsen.userservice.model.dto.UserDto;
import com.arsen.userservice.model.request.ChangeUserStatusRequest;
import com.arsen.userservice.model.request.CreateUserRequest;
import com.arsen.userservice.model.request.PasswordUpdateRequest;
import com.arsen.userservice.model.request.UserUpdateRequest;
import com.arsen.userservice.model.response.PageResponse;

public interface UserService {
    PageResponse<UserDto> findAllUsers(Integer page, Integer pageSize);
    UserDto findUserById(String userId);
    UserDto findUserByEmail(String email);
    UserDto createUser(CreateUserRequest createUserRequest);
    UserDto updateUserById(String userId, UserUpdateRequest userUpdateRequest);
    UserDto changeCurrentPasswordByUserId(String userId, PasswordUpdateRequest passwordUpdateRequest);
    void deleteUserById(String userId);
    UserDto changeUserStatus(String userId, ChangeUserStatusRequest changeUserStatusRequest);
}
