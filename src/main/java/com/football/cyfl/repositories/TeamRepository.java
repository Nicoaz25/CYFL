package com.football.cyfl.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.football.cyfl.models.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // Un método útil que seguro necesitas luego: buscar equipos por el ID de su liga
    List<Team> findByLeagueId(Long leagueId);
    
}