package com.subastas.subastas_api.repository;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.persona.mail = :mail")
    Optional<Usuario> findByMail(String mail);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.persona.mail = :mail")
    boolean existsByMail(String mail);

    @Query("SELECT u FROM Usuario u WHERE u.clienteLegacy = :cliente")
    Optional<Usuario> findByClienteLegacy(Cliente cliente);
}