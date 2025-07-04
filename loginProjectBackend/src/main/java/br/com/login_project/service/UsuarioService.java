package br.com.login_project.service;

import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.ResetPasswordDTO;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.exception.*;
import br.com.login_project.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final int MAX_TENTATIVAS = 5;
    private static final int BLOQUEIO_MINUTOS = 5;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new EmailJaRegistradoException("Email já está registrado");
        }
        Usuarios usuario = new Usuarios();
        usuario.setNomeCompleto(usuarioDTO.getNomeCompleto());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAt(null);

        Usuarios novoUsuario = usuarioRepository.save(usuario);
        UsuarioDTO respostaDTO = new UsuarioDTO();
        respostaDTO.setNomeCompleto(novoUsuario.getNomeCompleto());
        respostaDTO.setEmail(novoUsuario.getEmail());
        return respostaDTO;
    }

    public void alterarSenha(ResetPasswordDTO resetPasswordDTO) {
        Usuarios usuario = usuarioRepository.findByNomeCompletoAndEmail(resetPasswordDTO.getNomeCompleto(), resetPasswordDTO.getEmail())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com as credenciais fornecidas"));

        if (passwordEncoder.matches(resetPasswordDTO.getNovaSenha(), usuario.getSenha())) {
            throw new SenhaNaoPodeSerIgualAnteriorException("A nova senha não pode ser igual à senha anterior");
        }

        usuario.setSenha(passwordEncoder.encode(resetPasswordDTO.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    public Optional<Usuarios> login(String email, String senha) {
        Optional<Usuarios> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuarios usuario = usuarioOpt.get();
        verificarBloqueio(usuario);

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            incrementarTentativas(usuario);
            return Optional.empty();
        }

        resetarTentativasLogin(usuario);
        return Optional.of(usuario);
    }

    private void verificarBloqueio(Usuarios usuario) {
        if (usuario.getBloqueadoAt() != null) {
            long minutosBloqueio = ChronoUnit.MINUTES.between(usuario.getBloqueadoAt(), LocalDateTime.now());
            if (minutosBloqueio < BLOQUEIO_MINUTOS) {
                throw new IllegalStateException("Conta bloqueada. Tente novamente após " + (BLOQUEIO_MINUTOS - minutosBloqueio) + " minutos.");
            }
            // Desbloqueia a conta se o tempo já passou
            resetarTentativasLogin(usuario);
        }
    }

    private void incrementarTentativas(Usuarios usuario) {
        usuario.setTentativasLogin(usuario.getTentativasLogin() + 1);
        if (usuario.getTentativasLogin() >= MAX_TENTATIVAS) {
            usuario.setBloqueadoAt(LocalDateTime.now());
            logger.warn("Conta bloqueada para o usuário: {}", usuario.getEmail());
        }
        usuarioRepository.save(usuario);
    }

    private void resetarTentativasLogin(Usuarios usuario) {
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAt(null);
        usuarioRepository.save(usuario);
        logger.info("Login bem-sucedido para o usuário: {}", usuario.getEmail());
    }
}