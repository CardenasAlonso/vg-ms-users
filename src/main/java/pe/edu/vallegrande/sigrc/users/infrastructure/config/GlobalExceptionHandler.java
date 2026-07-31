package pe.edu.vallegrande.sigrc.users.infrastructure.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import pe.edu.vallegrande.sigrc.users.application.dto.common.ErrorResponse;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.KeycloakIntegrationException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.KeycloakRoleNotFoundException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.KeycloakUserAlreadyExistsException;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.NotFoundException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 404 - Not Found
    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(NotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()));
    }

    // 409 - Usuario duplicado en Keycloak
    @ExceptionHandler(KeycloakUserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleKeycloakConflict(KeycloakUserAlreadyExistsException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()));
    }

    // 400 - Rol inexistente en Keycloak
    @ExceptionHandler(KeycloakRoleNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleKeycloakRoleNotFound(KeycloakRoleNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()));
    }

    // 503 - Servicio de autenticación no disponible
    @ExceptionHandler(KeycloakIntegrationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleKeycloakIntegration(KeycloakIntegrationException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder()
                        .code(ex.getCode())
                        .message("Servicio de autenticación no disponible, intente más tarde")
                        .timestamp(LocalDateTime.now())
                        .build()));
    }

    // 400 - Reglas de negocio
    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDomain(DomainException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()));
    }

    // 400 - Validaciones (@NotBlank, @Email, etc.)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .code("VALIDATION_ERROR")
                        .message(errors)
                        .timestamp(LocalDateTime.now())
                        .build()));
    }

    // 500 - Error genérico
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .code("INTERNAL_ERROR")
                        .message("Error interno del servidor")
                        .timestamp(LocalDateTime.now())
                        .build()));
    }
}
