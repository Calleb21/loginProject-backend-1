package br.com.login_project.repository;

import br.com.login_project.domain.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByEmail(String email);

    Optional<Usuarios> findByNomeCompletoAndEmail(String nomeCompleto, String email);
}
