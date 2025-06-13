package br.com.login_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

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

}
