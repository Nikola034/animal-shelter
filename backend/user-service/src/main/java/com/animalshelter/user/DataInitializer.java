package com.animalshelter.user;

import com.animalshelter.user.model.User;
import com.animalshelter.user.model.UserRole;
import com.animalshelter.user.model.UserStatus;
import com.animalshelter.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByRole(UserRole.Admin)) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.Admin)
                    .status(UserStatus.Active)
                    .build();

            userRepository.save(admin);
            log.info("Default admin user created: username=admin, password=admin123");
        } else {
            log.info("Admin user already exists, skipping initialization");
        }
    }
}
