package br.com.login_project.exception;

public class ContaBloqueadaException extends IllegalStateException {

    public ContaBloqueadaException(String message) {
        super(message);
    }
}
