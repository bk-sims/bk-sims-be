package com.dalv.bksims.exceptionhandlers;

import com.dalv.bksims.exceptions.ActivityStatusViolationException;
import com.dalv.bksims.exceptions.ActivityTitleAlreadyExistsException;
import com.dalv.bksims.exceptions.AuthException;
import com.dalv.bksims.exceptions.CapacityLimitException;
import com.dalv.bksims.exceptions.EntityAlreadyExistsException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.exceptions.FieldBlankException;
import com.dalv.bksims.exceptions.FileTooLargeException;
import com.dalv.bksims.exceptions.InvalidDateFormatException;
import com.dalv.bksims.exceptions.InvalidDateRangeException;
import com.dalv.bksims.exceptions.InvalidFileExtensionException;
import com.dalv.bksims.exceptions.NoPermissionException;
import com.dalv.bksims.exceptions.ParticipantsNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ProblemDetail internalServerException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(AuthException.class)
    public ProblemDetail handleAuthException(AuthException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(ex.getStatus());
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", errors);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", "The request body is missing or not in the expected format");
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(ActivityTitleAlreadyExistsException.class)
    public ProblemDetail handleActivityTitleAlreadyExistsException(ActivityTitleAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(FieldBlankException.class)
    public ProblemDetail handleFieldBlankException(FieldBlankException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(InvalidDateFormatException.class)
    public ProblemDetail handleInvalidDateFormatException(InvalidDateFormatException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ProblemDetail handleInvalidDateRangeException(InvalidDateRangeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ProblemDetail handleFileTooLargeException(FileTooLargeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(InvalidFileExtensionException.class)
    public ProblemDetail handleInvalidFileExtensionException(InvalidFileExtensionException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(ActivityStatusViolationException.class)
    public ProblemDetail handleActivityStatusViolationException(ActivityStatusViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(ParticipantsNotFoundException.class)
    public ProblemDetail handleParticipantsNotFoundException(ParticipantsNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(IOException.class)
    public ProblemDetail handleIOException(IOException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setProperty("message", "An error occurred while processing the request.");
        return problemDetail;
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ProblemDetail handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(CapacityLimitException.class)
    public ProblemDetail handleCapacityLimitException(CapacityLimitException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }

    @ExceptionHandler(NoPermissionException.class)
    public ProblemDetail handleNoPermissionException(NoPermissionException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", ex.getLocalizedMessage());
        return problemDetail;
    }
}
