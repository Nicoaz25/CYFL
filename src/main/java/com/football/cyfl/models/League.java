package com.football.cyfl.models; // Acuérdate de poner tu paquete

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Set;

import com.football.cyfl.models.User;

@Entity
@Table(name = "leagues")
@Getter
@Setter
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // Relación: Cada liga la ha creado un usuario concreto
    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private User creador;

    // Relación: Una liga tiene una lista de muchos equipos
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Team> teams;

    // Relación Muchos a Muchos (Esto sustituye a la tabla league_access)
    @ManyToMany
    @JoinTable(
        name = "league_access",
        joinColumns = @JoinColumn(name = "league_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> gestoresPermitidos;
    
    public League() {
    }

    public League(String name, String description, User creador) {
        this.name = name;
        this.description = description;
        this.creador = creador;
    }
}