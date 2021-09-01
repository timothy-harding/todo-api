package com.ss.task.api.exception;

import javax.validation.ConstraintViolationException;

import com.ss.task.model.ApiError;
import com.ss.task.utils.ResponseEntityBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class TodoApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex,
            final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(final Exception ex, final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Object> handleTodoNotFoundException(final TodoNotFoundException ex) {
        return geResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(final InvalidRequestException ex) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenRequestException.class)
    public ResponseEntity<Object> handleForbiddenRequestException(final ForbiddenRequestException ex) {
        return geResponseEntity(ex, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        return geResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> geResponseEntity(final Throwable ex, final HttpStatus httpStatus) {
        log.error("error occurred", ex);
        final ApiError err = new ApiError().errorMessage(ex.getMessage()).status(httpStatus.name());
        return ResponseEntityBuilder.build(err);
    }

}