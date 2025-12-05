package com.Sistema.Aduana.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String role;
    private boolean enabled;
}
