package com.tumtech.groupcreationuserhngbackendtsk2.exception;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.ErrorResponse;
import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
public static ResponseEntity<APiResponses> handleUserNameNotFoundException (UserNameNotFoundException exception){
    return new ResponseEntity<>(new APiResponses("NOT_FOUND",exception.getMessage(),null,404), HttpStatus.NOT_FOUND);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public ResponseEntity<ErrorResponse> handleValidationExceptions (MethodArgumentNotValidException exception){
        ErrorResponse errorResponse = new ErrorResponse();
    Errors errors =  new Errors();
    exception.getBindingResult().getAllErrors().forEach(error->{

                String field = ((FieldError )error).getField();
                String errorMessage = error.getDefaultMessage();
           errors.setField(field);
           errors.setMessage(errorMessage);
            }

            );

        errorResponse.setErrors(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
}



}
