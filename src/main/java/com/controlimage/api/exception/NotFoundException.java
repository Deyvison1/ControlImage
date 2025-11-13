package com.controlimage.api.exception;

import java.io.Serial;

public class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException(String id) {
        super("Entity not found with id: " + id);
    }
}
