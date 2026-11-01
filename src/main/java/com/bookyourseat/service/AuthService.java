
package com.bookyourseat.service;

import com.bookyourseat.dto.SignupRequest;
import com.bookyourseat.repository.UserRepository;
import com.bookyourseat.entity.User;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // USER REGISTRATION
    public User registerUser(SignupRequest request) {

        if (request == null ||
                request.getFullName() == null ||
                request.getEmail() == null ||
                request.getPassword() == null) {

            throw new IllegalArgumentException(
                    "Please fill all required fields!"
            );
        }

        String email = request.getEmail().trim().toLowerCase();

        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email is required!");
        }

        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters!"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered! Please login."
            );
        }

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("ROLE_USER");

        // Save user in MySQL
        return userRepository.save(user);
    }

    // USER LOGIN
    public User loginUser(String email, String password) {

        if (email == null || email.isBlank() ||
                password == null || password.isBlank()) {

            throw new IllegalArgumentException(
                    "Email and password are required!"
            );
        }

        User user = userRepository.findByEmail(
                email.trim().toLowerCase()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Invalid email or password!"
                )
        );

        if (!passwordEncoder.matches(
                password, user.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid email or password!"
            );
        }

        return user;
    }
}
