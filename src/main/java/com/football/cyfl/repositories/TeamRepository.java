package com.football.cyfl.repositories;

import com.football.cyfl.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // Un método útil que seguro necesitas luego: buscar equipos por el ID de su liga
    List<Team> findByLeagueId(Long leagueId);
}