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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.football.cyfl.models.League;
import com.football.cyfl.models.Player;
import com.football.cyfl.models.Team;
import com.football.cyfl.models.User;
import com.football.cyfl.repositories.LeagueRepository;
import com.football.cyfl.repositories.PlayerRepository;
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

    @Autowired
    private PlayerRepository playerRepository;

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
    public String verDetalleLiga(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        League laLiga = leagueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La liga con ID " + id + " no existe."));

        String emailUsuarioLogueado = principal.getName();

        if (!laLiga.getCreador().getEmail().equals(emailUsuarioLogueado)) {
            return "redirect:/home?error=no_tienes_permiso";
        }

        // Buscamos pasando la entidad laLiga completa tal como está configurada en la
        // relación
        List<Team> equiposDeLaLiga = teamRepository.findByLeagueId(laLiga.getId());

        model.addAttribute("liga", laLiga);
        model.addAttribute("equipos", equiposDeLaLiga);

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

    // 🔥 NUEVO: Método GET para renderizar la pantalla de creación de Equipo
    @GetMapping("/liga/{leagueId}/crearEquipo")
    public String mostrarFormularioEquipo(@PathVariable Long leagueId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        League laLiga = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        if (!laLiga.getCreador().getEmail().equals(principal.getName())) {
            return "redirect:/home?error=no_tienes_permiso";
        }

        model.addAttribute("liga", laLiga);
        return "crearEquipo";
    }

    @PostMapping("/liga/{ligaId}/crearEquipo")
    public String guardarEquipo(@PathVariable Long ligaId,
            @RequestParam String name,
            @RequestParam String stadium,
            @RequestParam("file") MultipartFile file) {

        Team equipo = new Team();
        equipo.setName(name);
        equipo.setStadium(stadium);

        // Asociamos la liga
        League liga = leagueRepository.findById(ligaId).orElseThrow();
        equipo.setLeague(liga);

        // === LÓGICA OPTIMIZADA Y ROBUSTA PARA GUARDAR LA IMAGEN ===

        try {
            if (!file.isEmpty()) {

                String carpetaFotos = "src/main/resources/static/uploads/";
                Path rutaDirectorio = Paths.get(carpetaFotos);

                if (!Files.exists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                }

                String nombreFoto = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path rutaCompleta = Paths.get(carpetaFotos + nombreFoto);

                Files.write(rutaCompleta, file.getBytes());

                equipo.setLogo(nombreFoto);

            } else {
                equipo.setLogo("default.png");
            }

        } catch (IOException e) {
            System.out.println("❌ Error crítico al transferir la imagen: " + e.getMessage());
            e.printStackTrace();
            equipo.setLogo("default.png");
        }

        teamRepository.save(equipo);

        return "redirect:/liga/" + ligaId;
    }

    @GetMapping("/liga/{leagueId}/crearJugador")
    public String mostrarFormularioJugador(@PathVariable Long leagueId, Model model) {

        League liga = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        List<Team> equipos = teamRepository.findByLeagueId(leagueId);

        model.addAttribute("liga", liga);
        model.addAttribute("equipos", equipos);

        return "crearJugador";
    }

    @PostMapping("/liga/{leagueId}/crearJugador")
    public String guardarJugador(@PathVariable Long leagueId,
            @RequestParam("name") String name,
            @RequestParam("teamId") Long teamId,
            @RequestParam("position") String position,
            @RequestParam("dorsal") int dorsal,
            @RequestParam("age") int age,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        try {

            League laLiga = leagueRepository.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

            if (!laLiga.getCreador().getEmail().equals(principal.getName())) {
                return "redirect:/home?error=no_tienes_permiso";
            }

            Team elEquipo = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

            Player nuevoJugador = new Player();

            nuevoJugador.setName(name);
            nuevoJugador.setTeam(elEquipo);
            nuevoJugador.setPosition(position);
            nuevoJugador.setDorsal(dorsal);
            nuevoJugador.setAge(age);

            if (!file.isEmpty()) {

                String carpetaFotos = "src/main/resources/static/uploads/";
                Path rutaDirectorio = Paths.get(carpetaFotos);

                if (!Files.exists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                }

                String nombreFoto = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path rutaCompleta = Paths.get(carpetaFotos + nombreFoto);

                Files.write(rutaCompleta, file.getBytes());

                nuevoJugador.setLogo(nombreFoto);

            } else {
                nuevoJugador.setLogo("default.png");
            }

            playerRepository.save(nuevoJugador);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/liga/" + leagueId;
    }

    @GetMapping("/liga/{leagueId}/equipo/{teamId}")
    public String verDetalleEquipo(@PathVariable Long leagueId,
            @PathVariable Long teamId,
            Model model,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Team equipo = teamRepository.findById(teamId).orElseThrow();

        // 2. Buscamos solo los jugadores de este equipo
        List<Player> jugadores = playerRepository.findByTeamId(teamId);

        // 3. Enviamos todo al modelo del nuevo HTML
        model.addAttribute("ligaId", leagueId);
        model.addAttribute("equipo", equipo);
        model.addAttribute("jugadores", jugadores);

        return "verEquipo";
    }

    @GetMapping("/liga/{leagueId}/jugador/{playerId}")
    public String verPerfilJugador(@PathVariable Long leagueId,
            @PathVariable Long playerId,
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Player jugador = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        Team equipo = jugador.getTeam();

        model.addAttribute("ligaId", leagueId);
        model.addAttribute("jugador", jugador);
        model.addAttribute("equipo", equipo);

        return "perfil";
    }

    @GetMapping("/liga/{leagueId}/jugador/{playerId}/actualizarStats")
    public String mostrarFormularioStats(@PathVariable Long leagueId,
            @PathVariable Long playerId,
            Model model) {

        Player jugador = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        model.addAttribute("ligaId", leagueId);
        model.addAttribute("jugador", jugador);

        return "actualizarStats";
    }

    @PostMapping("/liga/{leagueId}/jugador/{playerId}/actualizarStats")
    public String actualizarStats(
            @PathVariable Long leagueId,
            @PathVariable Long playerId,
            @RequestParam int goles,
            @RequestParam int asistencias,
            @RequestParam int partidosGanados,
            @RequestParam int partidosEmpatados,
            @RequestParam int partidosPerdidos,
            @RequestParam int amarillas,
            @RequestParam int rojas) {

        Player jugador = playerRepository.findById(playerId)
                .orElseThrow();

        Team equipo = jugador.getTeam();

        // 🔥 SUMAR stats jugador
        jugador.setGoles(jugador.getGoles() + goles);
        jugador.setAsistencias(jugador.getAsistencias() + asistencias);
        int nuevosPartidos = jugador.getPartidosJugados() + partidosGanados + partidosEmpatados + partidosPerdidos;
        jugador.setPartidosJugados(nuevosPartidos);
        jugador.setPartidosGanados(jugador.getPartidosGanados() + partidosGanados);
        jugador.setPartidosEmpatados(jugador.getPartidosEmpatados() + partidosEmpatados);
        jugador.setPartidosPerdidos(jugador.getPartidosPerdidos() + partidosPerdidos);
        jugador.setTarjetasAmarillas(jugador.getTarjetasAmarillas() + amarillas);
        jugador.setTarjetasRojas(jugador.getTarjetasRojas() + rojas);

        // 🔥 PUNTOS
        int puntos = (partidosGanados * 3) +
                (partidosEmpatados * 1);

        jugador.setPuntos(jugador.getPuntos() + puntos);

        // 🔥 ACTUALIZAR EQUIPO
        equipo.setPartidosJugados(equipo.getPartidosJugados() + partidosGanados + partidosEmpatados + partidosPerdidos);
        equipo.setPartidosGanados(equipo.getPartidosGanados() + partidosGanados);
        equipo.setPartidosEmpatados(equipo.getPartidosEmpatados() + partidosEmpatados);
        equipo.setPartidosPerdidos(equipo.getPartidosPerdidos() + partidosPerdidos);
        equipo.setPuntos(equipo.getPuntos() + puntos);

        playerRepository.save(jugador);
        teamRepository.save(equipo);

        return "redirect:/liga/" + leagueId + "/jugador/" + playerId;
    }

    @GetMapping("/liga/{leagueId}/clasJugadores")
    public String clasificacionJugadores(
            @PathVariable Long leagueId,
            @RequestParam(defaultValue = "goles") String ordenarPor,
            Model model) {

        League liga = leagueRepository.findById(leagueId)
                .orElseThrow();

        List<Player> jugadores = playerRepository.findByTeamLeagueId(leagueId);

        // ORDENACIONES
        switch (ordenarPor) {

            case "asistencias":
                jugadores.sort((a, b) -> Integer.compare(b.getAsistencias(), a.getAsistencias()));
                break;

            case "puntos":
                jugadores.sort((a, b) -> Integer.compare(b.getPuntos(), a.getPuntos()));
                break;

            case "partidos":
                jugadores.sort((a, b) -> Integer.compare(b.getPartidosJugados(), a.getPartidosJugados()));
                break;

            default:
                jugadores.sort((a, b) -> Integer.compare(b.getGoles(), a.getGoles()));
                break;
        }

        model.addAttribute("liga", liga);
        model.addAttribute("jugadores", jugadores);
        model.addAttribute("ordenarPor", ordenarPor);

    

        return "clasJugadores";
    }
}