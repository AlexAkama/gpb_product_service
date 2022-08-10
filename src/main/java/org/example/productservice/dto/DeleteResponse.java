package org.example.productservice.dto;

public class DeleteResponse extends AppResponse {

    public DeleteResponse(String object) {
        super(String.format("%s удален удачно", object));
    }

}
