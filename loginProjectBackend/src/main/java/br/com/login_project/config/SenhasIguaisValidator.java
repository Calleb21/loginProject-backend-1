// Em: br/com/login_project/config/SenhasIguaisValidator.java
package br.com.login_project.config;

import anotacao.SenhasIguais;
import br.com.login_project.dto.PasswordAware;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


// Altere de UsuarioDTO para PasswordAware
public class SenhasIguaisValidator implements ConstraintValidator<SenhasIguais, PasswordAware> {

    @Override
    public boolean isValid(PasswordAware dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        // A lógica agora funciona para qualquer DTO que implemente PasswordAware
        return dto.getSenha() != null && dto.getSenha().equals(dto.getConfirmacaoSenha());
    }
}