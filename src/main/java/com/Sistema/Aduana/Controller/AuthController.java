package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.DTO.AuthRequest;
import com.Sistema.Aduana.DTO.AuthResponse;
import com.Sistema.Aduana.DTO.RegisterRequest;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Service.JwtService;
import com.Sistema.Aduana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(user);

            AuthResponse response = AuthResponse.builder()
                    .token(jwtToken)
                    .userId(user.getId())
                    .nombre(user.getNombre())
                    .apellido(user.getApellido())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .tipo(user.getRole().name().replace("ROLE_", "").toLowerCase())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);

            AuthResponse response = AuthResponse.builder()
                    .userId(user.getId())
                    .nombre(user.getNombre())
                    .apellido(user.getApellido())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .tipo(user.getRole().name().replace("ROLE_", "").toLowerCase())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario registrado exitosamente. ID: " + response.getUserId());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error en registro: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) authentication.getPrincipal();

        AuthResponse response = AuthResponse.builder()
                .userId(user.getId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .tipo(user.getRole().name().replace("ROLE_", "").toLowerCase())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }
}