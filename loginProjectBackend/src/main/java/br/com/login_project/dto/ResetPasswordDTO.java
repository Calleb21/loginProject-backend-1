package br.com.login_project.dto;

import anotacao.SenhasIguais;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@SenhasIguais(message = "A nova senha e a confirmação devem ser iguais")
@Data
public class ResetPasswordDTO implements PasswordAware {

    @NotBlank(message = "O nome completo é obrigatório para identificação.")
    private String nomeCompleto;

    @NotBlank(message = "O e-mail é obrigatório para identificação.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 11, message = "A nova senha deve ter no mínimo 11 caracteres.")
    private String novaSenha;

    @NotBlank(message = "A confirmação da nova senha é obrigatória.")
    private String confirmacaoNovaSenha;

    @Override
    @JsonIgnore
    public String getSenha() {
        return this.novaSenha;
    }

    @Override
    @JsonIgnore
    public String getConfirmacaoSenha() {
        return this.confirmacaoNovaSenha;
    }
}
