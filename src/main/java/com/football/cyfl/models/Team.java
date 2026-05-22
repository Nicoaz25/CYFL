package com.football.cyfl.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    private String logo;

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