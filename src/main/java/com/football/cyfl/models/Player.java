package com.football.cyfl.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter
@Setter
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int dorsal;
    private String position;
    private int age;
    private String logo;

    // Estadísticas de ataque/sanciones
    private int goles;
    private int asistencias;
    private int tarjetasAmarillas;
    private int tarjetasRojas;

    // Estadísticas de rendimiento (El pique individual)
    private int partidosJugados;
    private int partidosGanados;
    private int partidosEmpatados;
    private int partidosPerdidos;
    private int puntos;

    // Relación: El jugador pertenece de forma FIJA a un equipo
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    // Constructor vacío obligatorio
    public Player() {
    }

    // Constructor con los datos al crear al jugador
    public Player(String name, int dorsal, String position, int age, Team team) {
        this.name = name;
        this.dorsal = dorsal;
        this.position = position;
        this.age = age;
        this.team = team;
        
        // Iniciamos todo a cero al crearlo
        this.goles = 0;
        this.puntos = 0;
        this.partidosJugados = 0;
    }

    // ¡AQUÍ VAN LOS GETTERS Y SETTERS!
    // (Usa el atajo de VSCodium: Clic derecho -> Source Action -> Generate Getters and Setters)
}