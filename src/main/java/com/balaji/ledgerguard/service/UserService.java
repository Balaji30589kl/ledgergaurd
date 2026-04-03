package com.balaji.ledgerguard.service;

import java.util.List;

import com.balaji.ledgerguard.dto.request.CreateUserRequest;
import com.balaji.ledgerguard.dto.request.UpdateUserRequest;
import com.balaji.ledgerguard.dto.request.UpdateUserStatusRequest;
import com.balaji.ledgerguard.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request);
}
