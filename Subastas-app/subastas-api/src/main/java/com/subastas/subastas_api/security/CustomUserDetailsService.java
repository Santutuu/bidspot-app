package com.subastas.subastas_api.security;

import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario con mail: " + mail
                ));

        return User.builder()
                .username(usuario.getPersona().getMail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .build();
    }
}