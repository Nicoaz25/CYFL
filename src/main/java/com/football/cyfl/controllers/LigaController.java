package com.football.cyfl.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.football.cyfl.models.League;
import com.football.cyfl.models.Team;
import com.football.cyfl.models.User;
import com.football.cyfl.repositories.LeagueRepository;
import com.football.cyfl.repositories.TeamRepository;
import com.football.cyfl.repositories.UserRepository;

@Controller
public class LigaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping("/home")
    public String mostrarHome(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String emailUsuario = principal.getName();
        User usuarioLogueado = userRepository.findByEmail(emailUsuario).orElse(null);
        
        if (usuarioLogueado == null) {
            return "redirect:/login?error=usuario_no_encontrado";
        }
        
        List<League> misLigas = leagueRepository.findByCreador(usuarioLogueado);
        
        model.addAttribute("ligas", misLigas);
        model.addAttribute("emailUsuario", emailUsuario);
        return "home";
    }

    @GetMapping("/liga/{id}")
    public String verDetalleLiga(@PathVariable Long id, Model model) {
        League laLiga = leagueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La liga con ID " + id + " no existe."));

        model.addAttribute("liga", laLiga);
        return "liga";
    }

    @PostMapping("/nueva-liga")
    public String procesarCreacionLiga(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam("archivo") MultipartFile archivo, 
            Principal principal) {

        League nuevaLiga = new League();
        nuevaLiga.setName(nombre);
        nuevaLiga.setDescription(descripcion);

        String emailUsuario = principal.getName();
        User usuarioLogueado = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        nuevaLiga.setCreador(usuarioLogueado);

        if (!archivo.isEmpty()) {
            try {
                String carpetaFotos = "src/main/resources/static/uploads/";
                Path rutaDirectorio = Paths.get(carpetaFotos);

                if (!Files.exists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                }

                String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
                Path rutaCompleta = Paths.get(carpetaFotos + nombreArchivo);
                Files.write(rutaCompleta, archivo.getBytes());

                nuevaLiga.setLogo(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            nuevaLiga.setLogo("default.png");
        }

        leagueRepository.save(nuevaLiga);
        return "redirect:/home";
    }

    // ==========================================
    // 🔥 CAMBIO: RUTAS DEL FORMULARIO PROPIO 🔥
    // ==========================================

    // 1. MUESTRA LA PÁGINA /crearEquipo CON EL FONDO DE LA LIGA
    @GetMapping("/liga/{leagueId}/crearEquipo")
    public String mostrarFormularioEquipo(@PathVariable Long leagueId, Model model) {
        League laLiga = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        model.addAttribute("liga", laLiga); // Enviamos la liga para pintar el "fondo"
        model.addAttribute("leagueId", leagueId);
        model.addAttribute("team", new Team()); // El objeto para el formulario
        
        return "crearEquipo"; 
    }

    // 2. RECIBE LOS DATOS Y GUARDA
    @PostMapping("/liga/{leagueId}/crearEquipo")
    public String guardarEquipo(@PathVariable Long leagueId, 
                                @ModelAttribute Team team, 
                                @RequestParam("file") MultipartFile file) {
        try {
            League laLiga = leagueRepository.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            
            team.setLeague(laLiga);

            if (!file.isEmpty()) {
                String carpetaFotos = "src/main/resources/static/uploads/";
                Path rutaDirectorio = Paths.get(carpetaFotos);

                if (!Files.exists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                }

                String nombreFoto = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path rutaCompleta = Paths.get(carpetaFotos + nombreFoto);
                Files.write(rutaCompleta, file.getBytes());
                
                team.setLogo(nombreFoto);
            } else {
                team.setLogo("default-team.png");
            }

            teamRepository.save(team);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/liga/" + leagueId;
    }
}