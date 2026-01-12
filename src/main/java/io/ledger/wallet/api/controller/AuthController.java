package io.ledger.wallet.api.controller;

import io.ledger.wallet.api.dto.AuthResponseDto;
import io.ledger.wallet.api.dto.LoginRequestDto;
import io.ledger.wallet.api.dto.RegisterRequestDto;
import io.ledger.wallet.config.JwtUtil;
import io.ledger.wallet.domain.entity.AppUser;
import io.ledger.wallet.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager; 

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        AppUser user = userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully. ID: " + user.getId());
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponseDto(token));
        } else {
            throw new RuntimeException("Invalid Access");
        }
    }
    
    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Hello! You have successfully accessed a protected endpoint.");
    }
}