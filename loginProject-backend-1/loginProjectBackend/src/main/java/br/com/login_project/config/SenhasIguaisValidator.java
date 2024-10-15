package br.com.login_project.config;

import anotacao.SenhasIguais;
import br.com.login_project.dto.UsuarioDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class SenhasIguaisValidator implements ConstraintValidator<SenhasIguais, UsuarioDTO> {

    @Override
    public boolean isValid(UsuarioDTO usuarioDTO, ConstraintValidatorContext context) {
        // Verifica se as senhas são iguais
        return usuarioDTO.getSenha() != null && usuarioDTO.getSenha().equals(usuarioDTO.getConfirmacaoSenha());
    }
}
