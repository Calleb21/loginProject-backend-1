package br.com.login_project.testController;

import br.com.login_project.controller.UsuarioController;
import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;
    private Usuarios usuarios;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioDTO = new UsuarioDTO(1L, "Teste Teste", "teste@example.com", "Asd1234567@", "Asd1234567@");
        usuarios = new Usuarios(1L, "Teste Teste", "teste@example.com", "$2a$10$hashedPassword", 0, null);
    }

    @Test
    void registrar_Sucesso() {
        when(usuarioService.registrarUsuario(usuarioDTO)).thenReturn(usuarioDTO);

        ResponseEntity<?> response = usuarioController.registrar(usuarioDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuarioDTO, response.getBody());
        verify(usuarioService).registrarUsuario(usuarioDTO);
    }
}
