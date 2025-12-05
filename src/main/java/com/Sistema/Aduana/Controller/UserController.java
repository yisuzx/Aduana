package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.DTO.UserDTO;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Entity.UserRole;
import com.Sistema.Aduana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADUANA')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userService.convertToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        try {
            UserRole userRole = UserRole.valueOf(role);
            return ResponseEntity.ok(userService.findByRole(role));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (updates.containsKey("nombre")) {
                user.setNombre((String) updates.get("nombre"));
            }
            if (updates.containsKey("apellido")) {
                user.setApellido((String) updates.get("apellido"));
            }
            if (updates.containsKey("email")) {
                String newEmail = (String) updates.get("email");
                if (!newEmail.equals(user.getEmail()) && userService.existsByEmail(newEmail)) {
                    return ResponseEntity.badRequest().body("El email ya está en uso");
                }
                user.setEmail(newEmail);
            }
            if (updates.containsKey("role")) {
                try {
                    UserRole newRole = UserRole.valueOf((String) updates.get("role"));
                    user.setRole(newRole);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Rol inválido");
                }
            }
            if (updates.containsKey("enabled")) {
                user.setEnabled((Boolean) updates.get("enabled"));
            }

            User updatedUser = userService.save(user);
            return ResponseEntity.ok(userService.convertToDTO(updatedUser));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return updateUser(id, updates); // Reutilizamos el método PUT
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            userService.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado exitosamente");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (status.containsKey("enabled")) {
                user.setEnabled(status.get("enabled"));
                userService.save(user);

                String message = status.get("enabled") ? "Usuario activado" : "Usuario desactivado";
                return ResponseEntity.ok(message);
            }

            return ResponseEntity.badRequest().body("Campo 'enabled' requerido");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}