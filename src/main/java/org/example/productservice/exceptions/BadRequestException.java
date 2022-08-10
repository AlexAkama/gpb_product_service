package org.example.productservice.exceptions;

public class BadRequestException extends AbstractException {

    public BadRequestException(String description) {
        super(description);
    }

}
