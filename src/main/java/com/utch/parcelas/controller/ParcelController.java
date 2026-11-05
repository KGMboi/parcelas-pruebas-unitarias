package com.utch.parcelas.controller;

import com.utch.parcelas.model.Parcel;
import com.utch.parcelas.service.ParcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping
    public List<Parcel> findAll() {
        return parcelService.findAll();
    }

    @GetMapping("/{id}")
    public Parcel findById(@PathVariable UUID id) {
        return parcelService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Parcel> create(@RequestBody ParcelRequest request) {
        Parcel created = parcelService.create(
                new Parcel(request.name(), request.soilType(), request.ownerOrLead()));
        return ResponseEntity
                .created(URI.create("/api/v1/parcels/" + created.getId()))
                .body(created);
    }
}
