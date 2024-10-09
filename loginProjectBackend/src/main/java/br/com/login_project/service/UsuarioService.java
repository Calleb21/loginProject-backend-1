package br.com.login_project.service;

import br.com.login_project.domain.Usuarios;
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
    private static final long BLOQUEIO_MINUTAS = 5;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        // Verificar se o email já está registrado
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new EmailJaRegistradoException("Email já está registrado");
        }

        // Verificação de senha e confirmação de senha
        if (usuarioDTO.getSenha() == null || usuarioDTO.getConfirmacaoSenha() == null) {
            throw new SenhasNaoCoincidemException("Senha ou confirmação de senha é nula");
        }
        if (!usuarioDTO.getSenha().equals(usuarioDTO.getConfirmacaoSenha())) {
            throw new SenhasNaoCoincidemException("As senhas não coincidem");
        }

        // Restante do método
        Usuarios usuario = new Usuarios();
        usuario.setNomeCompleto(usuarioDTO.getNomeCompleto());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAt(null);

        Usuarios novoUsuario = usuarioRepository.save(usuario);
        return new UsuarioDTO(novoUsuario.getId(), novoUsuario.getNomeCompleto(), novoUsuario.getEmail(), null, null);
    }

    public void alterarSenha(String nomeCompleto, String email, String novaSenha, String confirmacaoSenha) {
        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new SenhasNaoCoincidemException("As senhas não coincidem");
        }

        Usuarios usuario = usuarioRepository.findByNomeCompletoAndEmail(nomeCompleto, email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        if (passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            throw new SenhaNaoPodeSerIgualAnteriorException("A nova senha não pode ser igual à senha anterior");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    // Método de login
    public Optional<Usuarios> login(String email, String senha) {
        Optional<Usuarios> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty(); // Usuário não encontrado
        }

        Usuarios usuario = usuarioOpt.get();
        verificarBloqueio(usuario); // Verificar bloqueio antes de processar o login

        // Verificar se a senha está correta
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            // Incrementar tentativas de login
            usuario.setTentativasLogin(usuario.getTentativasLogin() + 1);
            logger.warn("Tentativa de login falha para o usuário: {}", email);

            // Verificar se excedeu o limite de tentativas
            if (usuario.getTentativasLogin() >= MAX_TENTATIVAS) {
                usuario.setBloqueadoAt(LocalDateTime.now());
                logger.warn("Conta bloqueada para o usuário: {}", email);
            }

            usuarioRepository.save(usuario); // Salva o estado do usuário após falha de login
            return Optional.empty(); // Senha incorreta
        }

        // Login bem-sucedido: resetar tentativas de login
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAt(null);
        usuarioRepository.save(usuario); // Salva o estado do usuário após sucesso de login
        logger.info("Login bem-sucedido para o usuário: {}", email);
        return Optional.of(usuario); // Login bem-sucedido
    }

    private void verificarBloqueio(Usuarios usuario) {
        if (usuario.getBloqueadoAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            long minutosBloqueio = ChronoUnit.MINUTES.between(usuario.getBloqueadoAt(), now);
            if (minutosBloqueio < BLOQUEIO_MINUTAS) {
                throw new IllegalStateException("Conta bloqueada. Tente novamente após " + (BLOQUEIO_MINUTAS - minutosBloqueio) + " minutos.");
            } else {
                // Desbloquear o usuário após o período
                usuario.setBloqueadoAt(null);
                usuario.setTentativasLogin(0);
                usuarioRepository.save(usuario); // Salva o estado do usuário após desbloqueio
            }
        }
    }
}
