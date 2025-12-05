package com.Sistema.Aduana.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String password;
    private String role; // "ROLE_ADUANA", "ROLE_PDI", "ROLE_SAG"
}