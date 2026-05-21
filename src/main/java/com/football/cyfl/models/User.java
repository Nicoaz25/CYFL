package com.football.cyfl.models; // Acuérdate de cambiar esto por tu paquete real

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // Aquí se guardará "ADMIN" o "GESTOR"

    private int enabled;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    // Constructor vacío obligatorio para Hibernate/Spring
    public User() {
    }

    // Constructor para registrar nuevos usuarios (por defecto serán GESTOR)
    public User(String email, String nombre, String password) {
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.role = "GESTOR"; // Cualquiera que se registre entra como gestor
        this.enabled = 1;      // Cuenta activa por defecto
    }
}