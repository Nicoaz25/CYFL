package com.football.cyfl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter

public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String stadium;
    private int partidosJugados;
    private int partidosGanados;
    private int partidosEmpatados;
    private int partidosPerdidos;
    private int golesAFavor;
    private int golesEnContra;
    private int puntos;

    // Relación: Un equipo pertenece a una Liga (League)
    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    // Relación: Un equipo tiene muchos Jugadores (Players)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Player> players;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================
    
    // Constructor vacío obligatorio para Spring Boot
    public Team() {
    }

    // Constructor con los datos básicos
    public Team(String name, String stadium, League league) {
        this.name = name;
        this.stadium = stadium;
        this.league = league;
        this.partidosJugados = 0;
        this.puntos = 0;
    }


}