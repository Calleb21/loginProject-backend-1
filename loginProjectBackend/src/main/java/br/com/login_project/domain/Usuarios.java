package br.com.login_project.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "TB_USUARIO")
@NoArgsConstructor
@AllArgsConstructor
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "sq_usuario", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "NOME", nullable = false, length = 50)
    private String nomeCompleto;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "SENHA", nullable = false)
    @Size(min = 11, max = 15, message = "A senha deve ter entre 11 e 15 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "A senha deve conter ao menos uma letra maiúscula")
    @Pattern(regexp = ".*[0-9].*", message = "A senha deve conter ao menos um número")
    @Pattern(regexp = ".*[!@#$%^&*].*", message = "A senha deve conter ao menos um caractere especial")
    private String senha;

    @Column(name = "TENTATIVAS_LOGIN", nullable = false)
    private int tentativasLogin;

    @Column(name = "BLOQUEADO_AT", nullable = true)
    private LocalDateTime bloqueadoAt;

    public boolean isBloqueado() {
        if (bloqueadoAt != null) {
            return LocalDateTime.now().isBefore(bloqueadoAt.plusMinutes(5));
        }
        return false;
    }
}
