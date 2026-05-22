-- ====================================================================
-- CONFIGURACIÓN INICIAL
-- ====================================================================
DROP DATABASE IF EXISTS CYFL;
CREATE DATABASE IF NOT EXISTS CYFL;
USE CYFL;

-- ====================================================================
-- 1. SECCIÓN DE USUARIOS Y SEGURIDAD (Acceso a la Web simplificado)
-- ====================================================================

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'GESTOR', -- Puede ser 'ADMIN' o 'GESTOR'
    enabled TINYINT NOT NULL DEFAULT 1
);

-- ====================================================================
-- 2. SECCIÓN DE LIGAS Y CONTENEDORES
-- ====================================================================

CREATE TABLE leagues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    creador_id INT NOT NULL,
    FOREIGN KEY (creador_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE league_access (
    league_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (league_id, user_id),
    FOREIGN KEY (league_id) REFERENCES leagues(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ====================================================================
-- 3. SECCIÓN DE EQUIPOS Y JUGADORES
-- ====================================================================

CREATE TABLE teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stadium VARCHAR(100),
    partidos_jugados INT DEFAULT 0,
    partidos_ganados INT DEFAULT 0,
    partidos_empatados INT DEFAULT 0,
    partidos_perdidos INT DEFAULT 0,
    goles_a_favor INT DEFAULT 0,
    goles_en_contra INT DEFAULT 0,
    puntos INT DEFAULT 0,
    league_id INT NOT NULL,
    FOREIGN KEY (league_id) REFERENCES leagues(id) ON DELETE CASCADE
);

CREATE TABLE players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dorsal INT NOT NULL,
    position VARCHAR(10) NOT NULL,
    age INT,
    goles INT DEFAULT 0,               
    asistencias INT DEFAULT 0,         
    tarjetas_amarillas INT DEFAULT 0,
    tarjetas_rojas INT DEFAULT 0,
    partidos_jugados INT DEFAULT 0,    
    partidos_ganados INT DEFAULT 0,    
    partidos_empatados INT DEFAULT 0,  
    partidos_perdidos INT DEFAULT 0,   
    puntos INT DEFAULT 0,              
    team_id INT NOT NULL,              
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

