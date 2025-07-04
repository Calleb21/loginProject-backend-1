package br.com.login_project.exception;

import br.com.login_project.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {

    // Erros de validação (@Valid) - Retorna 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ApiErrorResponse(erros), HttpStatus.BAD_REQUEST);
    }

    // Erros de regras de negócio - Retorna 400 Bad Request
    @ExceptionHandler({EmailJaRegistradoException.class, SenhasNaoCoincidemException.class, SenhaNaoPodeSerIgualAnteriorException.class})
    public ResponseEntity<ApiErrorResponse> handleBusinessExceptions(RuntimeException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Erro de credenciais inválidas - Retorna 401 Unauthorized
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ApiErrorResponse> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    // Erro de conta bloqueada - Retorna 403 Forbidden
    @ExceptionHandler(ContaBloqueadaException.class)
    public ResponseEntity<ApiErrorResponse> handleContaBloqueada(ContaBloqueadaException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    // Erro de "não encontrado" - Retorna 404 Not Found
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(UsuarioNaoEncontradoException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}