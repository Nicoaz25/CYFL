package com.football.cyfl.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String paginaPrincipal(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // La etiqueta @AuthenticationPrincipal es una pasada: le pide a Spring Security 
        // los datos del usuario que acaba de iniciar sesión.
        
        // Pasamos el email del usuario a la vista para poder saludarle por su nombre/correo
        model.addAttribute("emailUsuario", userDetails.getUsername());
        
        return "home";
    }
}