package com.example.shop.service;

import com.example.shop.models.ShopUser; // Your application's User entity
import com.example.shop.repository.UserRepository; // JPA repository for User
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Overrides the Spring Security's `UserDetailsService` method to load user by username.
     * Fetches the user from the database and maps roles to `GrantedAuthority`.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database
        ShopUser shopUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Map roles to GrantedAuthority (Spring Security's role structure)
        List<GrantedAuthority> authorities = shopUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());

        // Return UserDetails for Spring Security
        return new User(
                shopUser.getUsername(),
                shopUser.getPassword(),
                authorities
        );
    }
}