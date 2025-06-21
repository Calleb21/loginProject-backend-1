package br.com.login_project.exception;

public class SenhasNaoCoincidemException extends RuntimeException {
    public SenhasNaoCoincidemException(String message) {
        super(message);
    }
}
