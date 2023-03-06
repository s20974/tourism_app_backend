package org.flywithme.controller;

import org.flywithme.apiError.ApiError;
import org.flywithme.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExistsExceptionResponseEntity(Exception e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse(new Date(), e.getMessage()),
                status
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFound(Exception e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse(new Date(), e.getMessage()),
                status
        );
    }
    @ExceptionHandler(IncorrectLoginOrPassword.class)
    public ResponseEntity<ErrorResponse> incorrectLoginOrPass(Exception e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse(new Date(), e.getMessage()),
                status
        );
    }

    @ExceptionHandler(EmailNotConfirmed.class)
    public ResponseEntity<ErrorResponse> emailNotConfirmed(Exception e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse(new Date(), e.getMessage()),
                status
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request){
        ApiError apiError = new ApiError(400, "Validation error", request.getServletPath());

        BindingResult result = exception.getBindingResult();
        Map<String, String> validationErrors = new HashMap<>();

        for(FieldError fieldError : result.getFieldErrors()){
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        apiError.setValidationErrors(validationErrors);

        return apiError;
    }
}
