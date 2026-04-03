package com.balaji.ledgerguard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.balaji.ledgerguard.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
