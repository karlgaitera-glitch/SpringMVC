package com.example.spring.spring_mvc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring.spring_mvc.entity.Auth_Role;

public interface AuthRoleRepository extends JpaRepository<Auth_Role, Long> {
    Optional<Auth_Role> findByName(String theUserName);

    Boolean existsByName(String theUserName);
}
