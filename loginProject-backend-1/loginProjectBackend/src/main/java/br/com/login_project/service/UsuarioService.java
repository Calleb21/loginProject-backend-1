package br.com.login_project.service;

import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Registro de novo usuário
    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já está registrado");
        }
        if (!usuarioDTO.getSenha().equals(usuarioDTO.getConfirmacaoSenha())) {
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        Usuarios usuario = new Usuarios();
        usuario.setNomeCompleto(usuarioDTO.getNomeCompleto());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        Usuarios novoUsuario = usuarioRepository.save(usuario);

        // Retornar o DTO do usuário registrado, sem a senha
        return new UsuarioDTO(novoUsuario.getId(), novoUsuario.getNomeCompleto(), novoUsuario.getEmail(), null, null);
    }

    public void alterarSenha(String nomeCompleto, String email, String novaSenha, String confirmacaoSenha) {
        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        Usuarios usuario = usuarioRepository.findByNomeCompletoAndEmail(nomeCompleto, email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    // Método de login (se necessário)
    public Optional<Usuarios> login(String email, String senha) {
        Optional<Usuarios> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && passwordEncoder.matches(senha, usuario.get().getSenha())) {
            return usuario;
        }
        return Optional.empty();
    }
}
