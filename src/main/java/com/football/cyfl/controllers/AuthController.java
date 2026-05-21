package com.football.cyfl.controllers;

import com.football.cyfl.models.User;
import com.football.cyfl.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String paginaInicio(Authentication auth) {
        // Si el usuario ya está logueado, lo mandamos directo a su panel
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/home";
        }
        // Si es un visitante nuevo, le enseñamos el index con los botones
        return "index"; 
    }

    @GetMapping("/login")
    public String paginaLogin(Authentication auth) {
        // Comprobamos si el usuario ya tiene sesión iniciada (el usuario anónimo no cuenta)
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/home"; // Si ya está dentro, lo mandamos al panel
        }
        return "login"; // Si no está logueado, le enseñamos la pantalla de login
    }

    @GetMapping("/registro")
    public String paginaRegistro(Authentication auth) {
        // Hacemos la misma comprobación para la página de registro
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/home";
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {
        try {
            User nuevoUsuario = new User();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(password);

            userService.registrarNuevoUsuario(nuevoUsuario);

            return "redirect:/login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }
}