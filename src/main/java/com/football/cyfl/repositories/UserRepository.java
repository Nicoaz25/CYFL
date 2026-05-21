package com.football.cyfl.repositories; // Cambia a tu paquete real

import com.football.cyfl.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Con solo escribir esto, Spring ya sabe que tiene que hacer un: 
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}