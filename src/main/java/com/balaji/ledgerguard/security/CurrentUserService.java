package com.balaji.ledgerguard.security;

import org.springframework.stereotype.Service;

import com.balaji.ledgerguard.entity.User;
import com.balaji.ledgerguard.repository.UserRepository;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User resolveActiveUser(String userIdHeader) {
        Long userId;
        try {
            userId = Long.valueOf(userIdHeader);
        } catch (NumberFormatException exception) {
            return null;
        }

        return userRepository.findById(userId).orElse(null);
    }
}
