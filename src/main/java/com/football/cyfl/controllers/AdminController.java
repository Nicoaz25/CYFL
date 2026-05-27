package com.football.cyfl.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.football.cyfl.models.League;
import com.football.cyfl.models.Player;
import com.football.cyfl.models.Team;
import com.football.cyfl.models.User;
import com.football.cyfl.repositories.LeagueRepository;
import com.football.cyfl.repositories.PlayerRepository;
import com.football.cyfl.repositories.TeamRepository;
import com.football.cyfl.repositories.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping
    public String adminPanel(Model model) {
        List<User> users = userRepository.findAll();
        List<League> leagues = leagueRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        List<Player> players = playerRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("leagues", leagues);
        model.addAttribute("teams", teams);
        model.addAttribute("players", players);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalLeagues", leagues.size());
        model.addAttribute("totalTeams", teams.size());
        model.addAttribute("totalPlayers", players.size());

        return "admin";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserEnabled(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setEnabled(user.getEnabled() == 1 ? 0 : 1);
        userRepository.save(user);

        return "redirect:/admin";
    }
}
