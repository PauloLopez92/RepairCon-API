package com.singlesoft.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.singlesoft.web.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByName(String name);
}
