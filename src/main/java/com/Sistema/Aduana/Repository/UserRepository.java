package com.Sistema.Aduana.Repository;

import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Métodos de búsqueda
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Métodos de existencia
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Métodos de filtrado
    List<User> findByRole(UserRole role);
    List<User> findByEnabledTrue();
    List<User> findByEnabledFalse();

    // Métodos de conteo
    long countByRole(UserRole role);
    long countByEnabledTrue();
    long countByEnabledFalse();

    // Búsquedas combinadas
    List<User> findByRoleAndEnabledTrue(UserRole role);
    List<User> findByNombreContainingIgnoreCase(String nombre);
    List<User> findByApellidoContainingIgnoreCase(String apellido);
    List<User> findByEmailContainingIgnoreCase(String email);

    // Métodos para estadísticas
    long countByRoleAndEnabledTrue(UserRole role);
}