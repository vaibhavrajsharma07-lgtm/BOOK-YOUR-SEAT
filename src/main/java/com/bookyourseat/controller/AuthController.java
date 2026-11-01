
package com.bookyourseat.controller;

import com.bookyourseat.dto.LoginRequest;
import com.bookyourseat.dto.SignupRequest;
import com.bookyourseat.entity.User;
import com.bookyourseat.service.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // SIGN UP
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest request,
            HttpSession session) {

        try {

            User user = authService.registerUser(request);

            // Create login session after registration
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFullName());

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Account created successfully!",
                            "userName", user.getFullName()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }


    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        try {

            User user = authService.loginUser(
                    request.getEmail(),
                    request.getPassword()
            );

            // Create login session
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFullName());

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Login successful!",
                            "userName", user.getFullName()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(401).body(
                    Map.of("message", e.getMessage())
            );
        }
    }


    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully!")
        );
    }


    // CHECK LOGIN STATUS
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {

            return ResponseEntity.status(401).body(
                    Map.of("message", "Please login first!")
            );
        }

        String userName = (String) session.getAttribute("userName");

        return ResponseEntity.ok(
                Map.of(
                        "userId", userId,
                        "userName", userName == null ? "" : userName
                )
        );
    }
}
