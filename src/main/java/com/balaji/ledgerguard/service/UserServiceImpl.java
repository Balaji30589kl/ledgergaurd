package com.balaji.ledgerguard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balaji.ledgerguard.dto.request.CreateUserRequest;
import com.balaji.ledgerguard.dto.request.UpdateUserRequest;
import com.balaji.ledgerguard.dto.request.UpdateUserStatusRequest;
import com.balaji.ledgerguard.dto.response.UserResponse;
import com.balaji.ledgerguard.entity.User;
import com.balaji.ledgerguard.enums.UserStatus;
import com.balaji.ledgerguard.exception.DuplicateEmailException;
import com.balaji.ledgerguard.exception.InvalidOperationException;
import com.balaji.ledgerguard.exception.UserNotFoundException;
import com.balaji.ledgerguard.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }

        User user = new User();
        user.setName(normalizeName(request.getName()));
        user.setEmail(normalizedEmail);
        user.setRole(request.getRole());
        user.setStatus(request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus());

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);

        boolean hasAnyFieldToUpdate = false;

        if (request.getName() != null) {
            user.setName(normalizeName(request.getName()));
            hasAnyFieldToUpdate = true;
        }

        if (request.getEmail() != null) {
            String normalizedEmail = normalizeEmail(request.getEmail());
            if (userRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id)) {
                throw new DuplicateEmailException(normalizedEmail);
            }
            user.setEmail(normalizedEmail);
            hasAnyFieldToUpdate = true;
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
            hasAnyFieldToUpdate = true;
        }

        if (!hasAnyFieldToUpdate) {
            throw new InvalidOperationException("At least one field (name, email, or role) must be provided for update");
        }

        User updatedUser = userRepository.save(user);
        return toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = findUserById(id);
        user.setStatus(request.getStatus());

        User updatedUser = userRepository.save(user);
        return toUserResponse(updatedUser);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
