package com.Sistema.Aduana.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String role;
    private String tipo; // "aduana", "pdi", "sag"
}