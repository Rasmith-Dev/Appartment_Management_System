package com.apartment.management.config;

import com.apartment.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AdminHealthIndicator implements HealthIndicator {
    private final UserRepository userRepository;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    public AdminHealthIndicator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Health health() {
        boolean exists = userRepository.existsByEmail(adminEmail);
        if (exists) {
            return Health.up()
                    .withDetail("adminEmail", adminEmail)
                    .withDetail("adminExists", true)
                    .build();
        }
        return Health.down()
                .withDetail("adminEmail", adminEmail)
                .withDetail("adminExists", false)
                .build();
    }
}
