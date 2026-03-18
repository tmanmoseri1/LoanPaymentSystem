package com.example.loanpayment.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

@Component
public class SecuritySeedRunner implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public SecuritySeedRunner(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .id(UUID.randomUUID())
                                .name(RoleName.ADMIN)
                                .build()
                ));

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .id(UUID.randomUUID())
                                .name(RoleName.CUSTOMER)
                                .build()
                ));

        String adminUser = System.getenv().getOrDefault("ADMIN_USERNAME", "admin");
        String adminPass = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123");

        userRepository.findByUsername(adminUser).orElseGet(() -> {
            User u = User.builder()
                    .id(UUID.randomUUID())
                    .username(adminUser)
                    .passwordHash(encoder.encode(adminPass))
                    .firstName("System")
                    .lastName("Admin")
                    .enabled(true)
                    .createdAt(Instant.now())
                    .build();

            if (u.getRoles() == null) {
                u.setRoles(new HashSet<>());
            }

            u.getRoles().add(adminRole);
            u.getRoles().add(customerRole);

            return userRepository.save(u);
        });
    }
}