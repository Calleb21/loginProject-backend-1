package br.com.login_project.controller;

import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Cadastro de novo Usuário
    @PostMapping("/signup")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        UsuarioDTO novoUsuario = usuarioService.registrarUsuario(usuarioDTO);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    // Rota de recuperação de senha
    @PostMapping("/resetPassword")
    public ResponseEntity<Void> esqueceuSenha(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        usuarioService.alterarSenha(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail(), usuarioDTO.getSenha(), usuarioDTO.getConfirmacaoSenha());
        return ResponseEntity.ok().build();
    }

    // Rota de Login
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String email, @RequestParam String senha) {
        Optional<Usuarios> usuarios = usuarioService.login(email, senha);
        if (usuarios.isPresent()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
