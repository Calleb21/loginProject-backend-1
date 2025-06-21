package br.com.login_project.controller;

import br.com.login_project.config.JwtUtil;
import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.LoginResponseDTO;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.exception.EmailJaRegistradoException;
import br.com.login_project.exception.SenhasNaoCoincidemException;
import br.com.login_project.exception.SenhaNaoPodeSerIgualAnteriorException;
import br.com.login_project.exception.UsuarioNaoEncontradoException;
import br.com.login_project.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    public UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    // Cadastro de novo Usuário
    @PostMapping("/signup")
    public ResponseEntity<?> registrar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO novoUsuario = usuarioService.registrarUsuario(usuarioDTO);
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
        } catch (EmailJaRegistradoException e) {
            // Retorna a mensagem específica da exceção para o frontend
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (SenhasNaoCoincidemException e) {
            // Retorna a mensagem específica da exceção para o frontend
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Loga a exceção para depuração no servidor
            System.err.println("Erro interno ao registrar usuário: " + e.getMessage());
            // Retorna uma mensagem genérica para outros erros inesperados
            return new ResponseEntity<>("Erro interno ao registrar usuário", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Rota de recuperação de senha
    @PostMapping("/resetPassword")
    public ResponseEntity<?> esqueceuSenha(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        try {
            usuarioService.alterarSenha(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail(), usuarioDTO.getSenha(), usuarioDTO.getConfirmacaoSenha());
            return ResponseEntity.ok().build();
        } catch (SenhasNaoCoincidemException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (SenhaNaoPodeSerIgualAnteriorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UsuarioNaoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Erro interno ao alterar senha: " + e.getMessage());
            return new ResponseEntity<>("Erro interno ao alterar senha", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Rota de Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginResponseDTO loginResponseDTO) {
        try {
            Optional<Usuarios> usuarios = usuarioService.login(loginResponseDTO.getEmail(), loginResponseDTO.getSenha());
            if (usuarios.isPresent()) {
                String token = jwtUtil.generateToken(usuarios.get());
                return ResponseEntity.ok(new LoginResponseDTO(token, usuarios.get().getNomeCompleto()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro interno no login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor ao tentar logar.");
        }
    }
}
