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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.football.cyfl.models.League;
import com.football.cyfl.models.User;
import com.football.cyfl.repositories.LeagueRepository;
import com.football.cyfl.repositories.UserRepository;

@Controller
public class LigaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    // NUEVO: Método para mostrar el Home con las ligas
    @GetMapping("/home")
    public String mostrarHome(Model model, Principal principal) {
        // Obtenemos todas las ligas de la base de datos
        List<League> misLigas = leagueRepository.findAll();
        
        // Se las pasamos al HTML
        model.addAttribute("ligas", misLigas);
        return "home"; // Carga tu archivo home.html
    }

    @PostMapping("/nueva-liga")
    public String procesarCreacionLiga(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam("archivo") MultipartFile archivo, // NUEVO: Recibe la imagen
            Principal principal) { 

        League nuevaLiga = new League();
        nuevaLiga.setName(nombre);
        nuevaLiga.setDescription(descripcion);

        String emailUsuario = principal.getName();
        User usuarioLogueado = userRepository.findByEmail(emailUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        nuevaLiga.setCreador(usuarioLogueado);

        // NUEVO: Lógica para guardar la imagen
        if (!archivo.isEmpty()) {
            try {
                // Carpeta donde se guardarán las fotos
                String carpetaFotos = "src/main/resources/static/uploads/";
                Path rutaDirectorio = Paths.get(carpetaFotos);
                
                // Si la carpeta no existe, la crea
                if (!Files.exists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                }

                // Generamos un nombre único y guardamos el archivo
                String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
                Path rutaCompleta = Paths.get(carpetaFotos + nombreArchivo);
                Files.write(rutaCompleta, archivo.getBytes());
                
                // Guardamos el nombre en la base de datos
                nuevaLiga.setLogo(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Si no sube foto, le ponemos una por defecto
            nuevaLiga.setLogo("default.png");
        }

        leagueRepository.save(nuevaLiga);
        return "redirect:/home";
    }
}