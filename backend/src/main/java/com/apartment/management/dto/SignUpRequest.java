package com.apartment.management.dto;

import com.apartment.management.model.User.Role;
import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
} 