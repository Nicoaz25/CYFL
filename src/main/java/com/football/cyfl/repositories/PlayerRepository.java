package com.football.cyfl.repositories;

import com.football.cyfl.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    // Para buscar todos los jugadores que pertenecen a un equipo concreto
    List<Player> findByTeamId(Long teamId);
}