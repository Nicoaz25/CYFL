package com.football.cyfl.repositories;

import com.football.cyfl.models.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.football.cyfl.models.User;
import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    List<League> findByCreador(User creador);
}