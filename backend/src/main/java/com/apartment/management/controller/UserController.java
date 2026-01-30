package com.apartment.management.controller;

import com.apartment.management.dto.UserSummary;
import com.apartment.management.model.User;
import com.apartment.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSummary>> getUsers(@RequestParam(required = false) User.Role role) {
        List<UserSummary> users = userRepository.findAll().stream()
                .filter(user -> role == null || user.getRole() == role)
                .map(UserSummary::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
