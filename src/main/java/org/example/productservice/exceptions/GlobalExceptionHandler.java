package org.example.productservice.exceptions;

import org.example.productservice.dto.AppResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<AppResponse> handleContentNotFound(NotFoundException e) {
        return new ResponseEntity<>(new AppResponse(e.getDescription()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<AppResponse> handleContentBadRequest(BadRequestException e) {
        return new ResponseEntity<>(new AppResponse(e.getDescription()), HttpStatus.BAD_REQUEST);
    }

}