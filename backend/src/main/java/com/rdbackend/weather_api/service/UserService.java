package com.rdbackend.weather_api.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rdbackend.weather_api.entity.User;
import com.rdbackend.weather_api.repo.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User upsertOAuthUser(
            String email,
            String name,
            String picture,
            String provider) {
        User user = userRepository.findByEmail(email)
                .map(existing -> {
                    existing.setName(name);
                    existing.setPicture(picture);
                    existing.setProvider(provider);
                    existing.setLastLoginAt(Instant.now());
                    return existing;
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setPicture(picture);
                    newUser.setProvider(provider);
                    newUser.setLastLoginAt(Instant.now());
                    return newUser;
                });
        return userRepository.save(user);
    }
}