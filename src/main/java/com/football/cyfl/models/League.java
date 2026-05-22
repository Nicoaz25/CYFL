package com.football.cyfl.models; // Acuérdate de poner tu paquete

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


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
    private String logo;

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