package com.apartment.management.security;

import com.apartment.management.model.User;
import com.apartment.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.error("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });
        
        logger.debug("Found user: {}", user.getUsername());
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        logger.debug("Attempting to load user by id: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("User not found with id: {}", id);
                return new UsernameNotFoundException("User not found with id: " + id);
            });
        
        logger.debug("Found user: {}", user.getUsername());
        return UserPrincipal.create(user);
    }
} 