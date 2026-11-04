package com.utch.parcelas.exception;

public class DuplicateParcelException extends RuntimeException {

    public DuplicateParcelException(String name) {
        super("Ya existe una parcela con el nombre \"" + name + "\".");
    }
}
