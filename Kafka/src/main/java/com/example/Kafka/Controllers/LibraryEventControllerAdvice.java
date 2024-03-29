package com.example.Kafka.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class LibraryEventControllerAdvice {
     @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?>handleRequestBody(MethodArgumentNotValidException ex){
//      List<FieldError> message = ex.getBindingResult().getFieldError()
         List<FieldError> message = ex.getBindingResult().getFieldErrors();
     String ErrorMessage = message.stream()
              .map(error->error.getField()+"-"+error.getDefaultMessage())
              .sorted()
              .collect(Collectors.joining(","));
     log.info("error message :{}",ErrorMessage);

     return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
