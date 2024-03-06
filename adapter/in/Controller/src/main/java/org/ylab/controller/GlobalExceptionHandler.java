package org.ylab.controller;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.ylab.domain.exception.ConflictException;
import org.ylab.domain.exception.NotFoundException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ProblemDetail errorDetail;

    @ExceptionHandler(value = {BadRequestException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequestException(final Exception exception) {
        log.error("An exception occurred!", exception);
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        errorDetail.setProperty("description", "Request details are not valid");

        return errorDetail;
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleBadCredentialsException(final BadCredentialsException exception) {
        log.error("An exception occurred!", exception);
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        errorDetail.setProperty("description", "The username or password is incorrect");

        return errorDetail;
    }

    @ExceptionHandler(value = {ConflictException.class,
            DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDataIntegrityViolationException(final Exception exception) {
        log.error("An exception occurred!", exception);
        if (exception instanceof ConflictException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        } else {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                    exception.getCause().getCause().getMessage());
        }

        errorDetail.setProperty("description", "Violates unique data constraint");

        return errorDetail;
    }

    @ExceptionHandler(value = {AccountStatusException.class,
            AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleForbiddenException(final RuntimeException exception) {
        log.error("An exception occurred!", exception);
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());

        if (exception instanceof AccountStatusException) {
            errorDetail.setProperty("description", "The account is locked");
        }
        if (exception instanceof AccessDeniedException) {
            errorDetail.setProperty("description", "You are not authorized to access this resource");
        }

        return errorDetail;
    }

    @ExceptionHandler(value = {NotFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFoundException(final Exception exception) {
        log.error("An exception occurred!", exception);
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        errorDetail.setProperty("description", "Requested resource was not found");

        return errorDetail;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleNotFoundException(final Throwable exception) {
        log.error("An exception occurred!", exception);
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorDetail.setProperty("description", "Unknown internal server error.");

        return errorDetail;
    }
}
