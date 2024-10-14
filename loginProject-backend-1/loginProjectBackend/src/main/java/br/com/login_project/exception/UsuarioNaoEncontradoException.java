package br.com.login_project.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UsuarioNaoEncontradoException extends UsernameNotFoundException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
