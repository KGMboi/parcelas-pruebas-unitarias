package com.utch.parcelas.service;

import com.utch.parcelas.exception.DuplicateParcelException;
import com.utch.parcelas.exception.ParcelNotFoundException;
import com.utch.parcelas.model.Parcel;
import com.utch.parcelas.repository.ParcelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    // Recupera todas las parcelas registradas
    public List<Parcel> findAll() {
        return parcelRepository.findAll();
    }

    // Recupera una parcela por su identificador
    public Parcel findById(UUID id) {
        return parcelRepository.findById(id)
                .orElseThrow(() -> new ParcelNotFoundException(id));
    }

    // Registra una parcela nueva: limpia el nombre, evita duplicados y la guarda
    public Parcel create(Parcel parcel) {
        if (parcel == null || parcel.getName() == null || parcel.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la parcela es obligatorio.");
        }

        String name = parcel.getName().trim();
        if (parcelRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateParcelException(name);
        }

        parcel.setName(name);
        return parcelRepository.save(parcel);
    }
}
