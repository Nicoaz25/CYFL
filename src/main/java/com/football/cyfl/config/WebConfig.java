package com.football.cyfl.config; 

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenemos la ruta absoluta de la carpeta de subidas en tu ordenador de casa
        String pathSubidas = Paths.get("src/main/resources/static/uploads/").toAbsolutePath().toUri().toString();
        
        // Le damos permiso al navegador para acceder directamente a esa carpeta
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(pathSubidas);
    }
}