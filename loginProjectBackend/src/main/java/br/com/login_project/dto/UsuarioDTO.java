package br.com.login_project.dto;

import anotacao.SenhasIguais;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;


@SenhasIguais(message = "A senha e a confirmação de senha devem ser iguais")
@Data
@NoArgsConstructor
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 50, message = "Nome completo deve ter no máximo 50 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 11, max = 15, message = "Senha deve ter entre 11 e 15 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "A senha deve conter ao menos uma letra maiúscula")
    @Pattern(regexp = ".*[0-9].*", message = "A senha deve conter ao menos um número")
    @Pattern(regexp = ".*[!@#$%^&*].*", message = "A senha deve conter ao menos um caractere especial")
    private String senha;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    @Size(min = 11, max = 15, message = "Confirmação de senha deve ter entre 11 e 15 caracteres")
    private String confirmacaoSenha;

    private int tentativasLogin;
    private boolean bloqueado;

    public UsuarioDTO(Long id, String nomeCompleto, String email, String senha, String confirmacaoSenha) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.confirmacaoSenha = confirmacaoSenha;
    }
}