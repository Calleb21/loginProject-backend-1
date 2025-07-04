package br.com.login_project.exception;

import javax.naming.AuthenticationException;

public class CredenciaisInvalidasException extends AuthenticationException {

    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}
