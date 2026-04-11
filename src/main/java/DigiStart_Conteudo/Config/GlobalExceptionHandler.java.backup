package DigiStart_Conteudo.Config;

import DigiStart_Conteudo.Exceptions.RecurosNaoEncontrado;
import DigiStart_Conteudo.Exceptions.ValidacaoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import DigiStart_Conteudo.Exceptions.RegraNegocioException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecurosNaoEncontrado.class)
    public ResponseEntity<String> handleNotFound(RecurosNaoEncontrado ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<String> handleValidation(ValidacaoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<String> handleBusiness(RegraNegocioException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}