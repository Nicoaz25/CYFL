-- ====================================================================
-- CONFIGURACIÓN INICIAL
-- ====================================================================
DROP DATABASE IF EXISTS CYFL;

CREATE DATABASE IF NOT EXISTS CYFL;
USE CYFL;

-- ====================================
-- 1. SECCIÓN DE USUARIOS Y SEGURIDAD (Acceso a la Web)
-- ====================================================================

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ====================================================================
-- 2. SECCIÓN DE LIGAS Y CONTENEDORES (Estructura Organizativa)
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

-- Los equipos ahora actúan como etiquetas o contenedores para los partidos (ej: 'Equipo Rojo', 'Equipo Azul')
CREATE TABLE teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stadium VARCHAR(100),
    league_id INT NOT NULL,
    FOREIGN KEY (league_id) REFERENCES leagues(id) ON DELETE CASCADE
);

-- ====================================================================
-- 3. SECCIÓN DE JUGADORES (Estadísticas Individuales Acumuladas)
-- ====================================================================

-- El jugador es LIBRE, no tiene clave foránea de ningún equipo (team_id eliminado)
CREATE TABLE players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dorsal INT NOT NULL,
    position VARCHAR(10) NOT NULL, -- 'POR', 'DEF', 'CEN', 'DEL'
    age INT,
    -- Estadísticas clásicas
    goles INT DEFAULT 0,
    asistencias INT DEFAULT 0,
    tarjetas_amarillas INT DEFAULT 0,
    tarjetas_rojas INT DEFAULT 0,
    -- Estadísticas para tu Sistema de Clasificación
    partidos_jugados INT DEFAULT 0,
    partidos_ganados INT DEFAULT 0,
    partidos_empatados INT DEFAULT 0,
    partidos_perdidos INT DEFAULT 0,
    puntos INT DEFAULT 0             -- Suma de puntos (ej: Victoria = 3pts, Empate = 1pt)
);

-- ====================================================================
-- 4. SECCIÓN DE DINÁMICA DE PARTIDOS (El Núcleo de las Pachangas)
-- ====================================================================

-- Registra el evento del partido y el marcador global
CREATE TABLE matches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_match DATE NOT NULL,
    team_home_id INT NOT NULL,       -- Equipo Local ese día
    team_away_id INT NOT NULL,       -- Equipo Visitante ese día
    goals_home INT DEFAULT 0,
    goals_away INT DEFAULT 0,
    league_id INT NOT NULL,
    FOREIGN KEY (team_home_id) REFERENCES teams(id) ON DELETE CASCADE,
    FOREIGN KEY (team_away_id) REFERENCES teams(id) ON DELETE CASCADE,
    FOREIGN KEY (league_id) REFERENCES leagues(id) ON DELETE CASCADE
);

-- TABLA MÁGICA: Guarda qué jugador jugó en qué equipo para UN partido concreto (Muchos a Muchos)
CREATE TABLE match_players (
    match_id INT NOT NULL,
    player_id INT NOT NULL,
    team_id INT NOT NULL,            -- El equipo del jugador durante ESTE partido
    PRIMARY KEY (match_id, player_id),
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

