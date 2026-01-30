package com.apartment.management.config;

import com.apartment.management.model.User;
import com.apartment.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        User admin = userRepository.findByEmail(adminEmail).orElse(null);
        if (admin == null) {
            User created = new User();
            created.setEmail(adminEmail);
            created.setUsername(adminUsername);
            created.setPassword(passwordEncoder.encode(adminPassword));
            created.setRole(User.Role.ADMIN);
            userRepository.save(created);
            logger.info("Admin user created for email {}", adminEmail);
            return;
        }

        boolean passwordMatches = passwordEncoder.matches(adminPassword, admin.getPassword());
        boolean roleMatches = admin.getRole() == User.Role.ADMIN;

        if (!passwordMatches || !roleMatches) {
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            logger.info("Admin user updated for email {}", adminEmail);
        } else {
            logger.info("Admin user already valid for email {}", adminEmail);
        }
    }
}
