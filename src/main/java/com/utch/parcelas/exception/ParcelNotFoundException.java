package com.utch.parcelas.exception;

import java.util.UUID;

public class ParcelNotFoundException extends RuntimeException {

    public ParcelNotFoundException(UUID id) {
        super("No existe la parcela con id " + id + ".");
    }
}
