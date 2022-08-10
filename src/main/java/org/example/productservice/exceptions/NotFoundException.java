package org.example.productservice.exceptions;


public class NotFoundException extends AbstractException {

    public NotFoundException(String name, Long id) {
        super(String.format("%s id=%d не найден", name, id));
    }

}
