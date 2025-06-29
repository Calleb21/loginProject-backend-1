package br.com.login_project.controller;

import br.com.login_project.config.JwtUtil;
import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.LoginResponseDTO;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.exception.EmailJaRegistradoException;
import br.com.login_project.exception.SenhasNaoCoincidemException;
import br.com.login_project.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/usuarios" )
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
            return new ResponseEntity<>("Email já registrado", HttpStatus.BAD_REQUEST);
        } catch (SenhasNaoCoincidemException e) {
            return new ResponseEntity<>("As senhas não coincidem", HttpStatus.BAD_REQUEST);
        }
    }

    // Rota de recuperação de senha
    @PostMapping("/resetPassword")
    public ResponseEntity<?> esqueceuSenha(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        try {
            usuarioService.alterarSenha(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail(), usuarioDTO.getSenha(), usuarioDTO.getConfirmacaoSenha());
            return ResponseEntity.ok("Senha alterada com sucesso.");
        } catch (SenhasNaoCoincidemException e) {
            return new ResponseEntity<>("As senhas não coincidem", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao alterar senha", HttpStatus.INTERNAL_SERVER_ERROR);
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
        }
    }

}
