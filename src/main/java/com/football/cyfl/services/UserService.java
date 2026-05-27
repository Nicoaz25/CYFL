package com.football.cyfl.services;

import com.football.cyfl.models.User;
import com.football.cyfl.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService { 

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Tu método de registro de antes (se queda igual)
    public User registrarNuevoUsuario(User nuevoUsuario) {
        Optional<User> usuarioExistente = userRepository.findByEmail(nuevoUsuario.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Error: Este correo ya está registrado.");
        }
        String contrasenaEncriptada = passwordEncoder.encode(nuevoUsuario.getPassword());
        nuevoUsuario.setPassword(contrasenaEncriptada);
        nuevoUsuario.setRole("GESTOR");
        nuevoUsuario.setEnabled(1);
        return userRepository.save(nuevoUsuario);
    }

    // ====================================================================
    // ¡NUEVO MÉTODO! El que usa Spring Security para hacer el Login
    // ====================================================================
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario con email: " + email));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getEnabled() == 1,
                true,
                true,
                true,
                new ArrayList<>(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole())))
        );
    }
}