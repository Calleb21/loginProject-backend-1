package br.com.login_project.testService;

import br.com.login_project.domain.Usuarios;
import br.com.login_project.dto.UsuarioDTO;
import br.com.login_project.exception.*;
import br.com.login_project.repository.UsuarioRepository;
import br.com.login_project.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioDTO usuarioDTO;
    private Usuarios usuarios;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioDTO = new UsuarioDTO(1L, "Teste Teste", "teste@example.com", "Asd1234567@", "Asd1234567@");
        usuarios = new Usuarios(1L, "Teste Teste", "teste@example.com", "$2a$10$hashedPassword", 0, null);
    }

    @Test
    void registrarUsuario_Sucesso() {
        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuarioDTO.getSenha())).thenReturn("$2a$10$hashedPassword");
        when(usuarioRepository.save(any(Usuarios.class))).thenReturn(usuarios);

        UsuarioDTO result = usuarioService.registrarUsuario(usuarioDTO);

        assertNotNull(result);
        assertEquals(usuarioDTO.getEmail(), result.getEmail());
        verify(usuarioRepository).save(any(Usuarios.class));
    }

    @Test
    void registrarUsuario_EmailJaRegistrado() {
        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.of(usuarios));

        assertThrows(EmailJaRegistradoException.class, () -> usuarioService.registrarUsuario(usuarioDTO));
    }

    @Test
    void registrarUsuario_SenhasNaoCoincidem() {
        usuarioDTO.setConfirmacaoSenha("SenhaDiferente");

        assertThrows(SenhasNaoCoincidemException.class, () -> usuarioService.registrarUsuario(usuarioDTO));
    }

    @Test
    void alterarSenha_Sucesso() {
        when(usuarioRepository.findByNomeCompletoAndEmail(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail()))
                .thenReturn(Optional.of(usuarios));
        when(passwordEncoder.matches(usuarioDTO.getSenha(), usuarios.getSenha())).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("$2a$10$newPasswordHash");

        assertDoesNotThrow(() -> usuarioService.alterarSenha(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail(), usuarioDTO.getSenha(), usuarioDTO.getConfirmacaoSenha()));
        verify(usuarioRepository).save(usuarios);
    }

    @Test
    void alterarSenha_UsuarioNaoEncontrado() {
        when(usuarioRepository.findByNomeCompletoAndEmail(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.alterarSenha(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail(), usuarioDTO.getSenha(), usuarioDTO.getConfirmacaoSenha()));
    }

    @Test
    void alterarSenha_SenhasNaoCoincidem() {
        assertThrows(SenhasNaoCoincidemException.class, () -> usuarioService.alterarSenha(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail(), "NovaSenha", "OutraSenha"));
    }

    @Test
    void alterarSenha_SenhaIgualAnterior() {
        when(usuarioRepository.findByNomeCompletoAndEmail(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail()))
                .thenReturn(Optional.of(usuarios));
        when(passwordEncoder.matches(usuarioDTO.getSenha(), usuarios.getSenha())).thenReturn(true);

        assertThrows(SenhaNaoPodeSerIgualAnteriorException.class, () -> usuarioService.alterarSenha(usuarioDTO.getNomeCompleto(), usuarioDTO.getEmail(), usuarioDTO.getSenha(), usuarioDTO.getConfirmacaoSenha()));
    }

    @Test
    void registrarUsuario_SenhaNula() {
        usuarioDTO.setSenha(null);
        usuarioDTO.setConfirmacaoSenha(null);

        assertThrows(SenhasNaoCoincidemException.class, () -> usuarioService.registrarUsuario(usuarioDTO));
    }

    @Test
    void login_ContaBloqueada_TentativaLogin() {
        usuarios.setTentativasLogin(5);
        usuarios.setBloqueadoAt(LocalDateTime.now());

        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.of(usuarios));

        assertThrows(IllegalStateException.class, () -> usuarioService.login(usuarioDTO.getEmail(), usuarioDTO.getSenha()));
    }

    @Test
    void login_Sucesso() {
        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.of(usuarios));
        when(passwordEncoder.matches(usuarioDTO.getSenha(), usuarios.getSenha())).thenReturn(true);

        Optional<Usuarios> result = usuarioService.login(usuarioDTO.getEmail(), usuarioDTO.getSenha());

        assertTrue(result.isPresent());
        verify(usuarioRepository).save(usuarios);
    }

    @Test
    void login_EmailNaoEncontrado() {
        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.empty());

        Optional<Usuarios> result = usuarioService.login(usuarioDTO.getEmail(), usuarioDTO.getSenha());

        assertTrue(result.isEmpty());
    }

    @Test
    void login_SenhaIncorreta() {
        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.of(usuarios));
        when(passwordEncoder.matches(usuarioDTO.getSenha(), usuarios.getSenha())).thenReturn(false);

        Optional<Usuarios> result = usuarioService.login(usuarioDTO.getEmail(), usuarioDTO.getSenha());

        assertTrue(result.isEmpty());
        verify(usuarioRepository).save(usuarios);
    }
/*
    @Test
    void login_BloqueioPorTentativasExcedidas() {
        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.of(usuarios));

        // Simulando 4 tentativas de login incorretas
        for (int i = 0; i < 4; i++) {
            when(passwordEncoder.matches(usuarioDTO.getSenha(), usuarios.getSenha())).thenReturn(false);
            assertDoesNotThrow(() -> usuarioService.login(usuarioDTO.getEmail(), "senhaErrada"));
        }

        // Na quinta tentativa, a conta deve ser bloqueada
        when(passwordEncoder.matches(usuarioDTO.getSenha(), usuarios.getSenha())).thenReturn(false);

        assertThrows(ContaBloqueadaException.class, () -> usuarioService.login(usuarioDTO.getEmail(), "senhaErrada"));
        verify(usuarioRepository).save(usuarios);
    }

    @Test
    void verificarBloqueio_DesbloqueioAutomatico() {
        usuarios.setTentativasLogin(5);
        usuarios.setBloqueadoAt(LocalDateTime.now().minusMinutes(6));

        when(usuarioRepository.findByEmail(usuarioDTO.getEmail())).thenReturn(Optional.of(usuarios));

        assertDoesNotThrow(() -> usuarioService.login(usuarioDTO.getEmail(), usuarioDTO.getSenha()));

        assertNull(usuarios.getBloqueadoAt());
        assertEquals(0, usuarios.getTentativasLogin());
        verify(usuarioRepository).save(usuarios);
    }

 */
}
