package com.apartment.management.dto;

import com.apartment.management.model.User;
import lombok.Data;

@Data
public class UserSummary {
    private Long id;
    private String username;
    private String email;
    private User.Role role;

    public static UserSummary from(User user) {
        UserSummary summary = new UserSummary();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setEmail(user.getEmail());
        summary.setRole(user.getRole());
        return summary;
    }
}
