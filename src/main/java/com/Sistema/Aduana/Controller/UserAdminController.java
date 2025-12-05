package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.DTO.UserDTO;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Entity.UserRole;
import com.Sistema.Aduana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            UserDTO userDTO = userService.convertToDTO(userOptional.get());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado con ID: " + id));
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {

            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El usuario '" + request.getUsername() + "' ya existe"));
            }


            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El email '" + request.getEmail() + "' ya está registrado"));
            }


            User user = new User();
            user.setNombre(request.getNombre());
            user.setApellido(request.getApellido());
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setRole(UserRole.valueOf(request.getRole()));
            user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

            User savedUser = userService.save(user);
            UserDTO userDTO = userService.convertToDTO(savedUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rol inválido. Roles válidos: ROLE_ADUANA, ROLE_PDI, ROLE_SAG"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            // Actualizar campos
            if (request.getNombre() != null) user.setNombre(request.getNombre());
            if (request.getApellido() != null) user.setApellido(request.getApellido());

            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                if (userService.existsByEmail(request.getEmail())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "El email '" + request.getEmail() + "' ya está en uso"));
                }
                user.setEmail(request.getEmail());
            }

            if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
                if (userService.existsByUsername(request.getUsername())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "El usuario '" + request.getUsername() + "' ya existe"));
                }
                user.setUsername(request.getUsername());
            }

            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                user.setPassword(request.getPassword()); // Se encripta en save()
            }

            if (request.getRole() != null) {
                try {
                    user.setRole(UserRole.valueOf(request.getRole()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Rol inválido: " + request.getRole()));
                }
            }

            if (request.getEnabled() != null) {
                user.setEnabled(request.getEnabled());
            }

            User updatedUser = userService.save(user);
            return ResponseEntity.ok(userService.convertToDTO(updatedUser));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar usuario: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            updates.forEach((key, value) -> {
                switch (key) {
                    case "nombre":
                        user.setNombre((String) value);
                        break;
                    case "apellido":
                        user.setApellido((String) value);
                        break;
                    case "email":
                        String newEmail = (String) value;
                        if (!newEmail.equals(user.getEmail()) && userService.existsByEmail(newEmail)) {
                            throw new RuntimeException("El email '" + newEmail + "' ya está en uso");
                        }
                        user.setEmail(newEmail);
                        break;
                    case "username":
                        String newUsername = (String) value;
                        if (!newUsername.equals(user.getUsername()) && userService.existsByUsername(newUsername)) {
                            throw new RuntimeException("El usuario '" + newUsername + "' ya existe");
                        }
                        user.setUsername(newUsername);
                        break;
                    case "password":
                        user.setPassword((String) value);
                        break;
                    case "role":
                        try {
                            user.setRole(UserRole.valueOf((String) value));
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Rol inválido: " + value);
                        }
                        break;
                    case "enabled":
                        user.setEnabled((Boolean) value);
                        break;
                }
            });

            User updatedUser = userService.save(user);
            return ResponseEntity.ok(userService.convertToDTO(updatedUser));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar usuario: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado con ID: " + id));
            }

            userService.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuario eliminado exitosamente",
                    "id", id
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            List<UserDTO> users = userService.findByRole(role);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rol inválido. Roles válidos: ROLE_ADUANA, ROLE_PDI, ROLE_SAG"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            if (status.containsKey("enabled")) {
                boolean newStatus = status.get("enabled");
                user.setEnabled(newStatus);
                userService.save(user);

                String message = newStatus ? "Usuario activado" : "Usuario desactivado";
                return ResponseEntity.ok(Map.of(
                        "message", message,
                        "id", id,
                        "enabled", newStatus
                ));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Campo 'enabled' requerido"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countUsers() {
        long total = userService.countAll();
        long aduana = userService.countByRole("ROLE_ADUANA");
        long pdi = userService.countByRole("ROLE_PDI");
        long sag = userService.countByRole("ROLE_SAG");

        return ResponseEntity.ok(Map.of(
                "total", total,
                "aduana", aduana,
                "pdi", pdi,
                "sag", sag,
                "activos", userService.countEnabled()
        ));
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CreateUserRequest {
        private String nombre;
        private String apellido;
        private String email;
        private String username;
        private String password;
        private String role; // ROLE_ADUANA, ROLE_PDI, ROLE_SAG, ROLE_ADMIN
        private Boolean enabled;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UpdateUserRequest {
        private String nombre;
        private String apellido;
        private String email;
        private String username;
        private String password;
        private String role;
        private Boolean enabled;
    }
}
