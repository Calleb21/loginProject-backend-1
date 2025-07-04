package br.com.login_project.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ApiErrorResponse {

    private final List<String> erros;

    public ApiErrorResponse(String erro) {
        this.erros = Arrays.asList(erro);
    }

    public ApiErrorResponse(List<String> erros) {
        this.erros = erros;
    }
}
