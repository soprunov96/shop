package com.example.shop.repository;

import com.example.shop.models.ShopUser;
import org.springframework.security.core.userdetails.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ShopUser, Long> {
    Optional<ShopUser> findByUsername(String username);
}