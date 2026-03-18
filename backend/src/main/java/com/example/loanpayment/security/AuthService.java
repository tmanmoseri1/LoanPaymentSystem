package com.example.loanpayment.security;

import com.example.loanpayment.common.BadRequestException;
import com.example.loanpayment.security.dto.AuthResponse;
import com.example.loanpayment.security.dto.LoginRequest;
import com.example.loanpayment.security.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       AuthenticationManager authManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void registerCustomer(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BadRequestException("Username already exists");
        }

        Role customer = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .id(UUID.randomUUID())
                        .name(RoleName.CUSTOMER)
                        .build()));

        User u = User.builder()
                .id(UUID.randomUUID())
                .username(req.username())
                .passwordHash(encoder.encode(req.password()))
                .firstName(req.firstName())
                .lastName(req.lastName())
                .enabled(true)
                .createdAt(Instant.now())
                .build();

        u.getRoles().add(customer);
        userRepository.save(u);
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        User u = userRepository.findByUsername(req.username()).orElseThrow();
        List<String> roles = u.getRoles().stream().map(r -> r.getName().name()).toList();
        String token = jwtService.generateToken(u.getUsername(), roles);
        return new AuthResponse(token, u.getUsername(), roles);
    }
}
