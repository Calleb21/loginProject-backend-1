package br.com.login_project.controller;

import br.com.login_project.config.JwtUtil;
import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.LoginRequestDTO;
import br.com.login_project.dto.LoginResponseDTO;
import br.com.login_project.dto.ResetPasswordDTO;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.exception.CredenciaisInvalidasException;
import br.com.login_project.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para cadastro, autenticação e recuperação de senha.")
public class UsuarioController {

    @Autowired
    public UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Cadastra um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (e.g., e-mail já existe, senhas não coincidem)")
    })
    @PostMapping("/signup")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        UsuarioDTO novoUsuario = usuarioService.registrarUsuario(usuarioDTO);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @Operation(summary = "Altera a senha de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (e.g., senha igual à anterior)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping("/resetPassword")
    public ResponseEntity<Void> esqueceuSenha(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        usuarioService.alterarSenha(resetPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Conta temporariamente bloqueada")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) throws CredenciaisInvalidasException {
        Usuarios usuario = usuarioService.login(loginRequestDTO.getEmail(), loginRequestDTO.getSenha())
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos."));

        String token = jwtUtil.generateToken(usuario);
        return ResponseEntity.ok(new LoginResponseDTO(token, usuario.getNomeCompleto()));
    }
}