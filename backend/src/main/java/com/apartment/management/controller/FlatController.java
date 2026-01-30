package com.apartment.management.controller;

import com.apartment.management.model.Flat;
import com.apartment.management.service.FlatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flats")
@CrossOrigin(origins = "http://localhost:3000")
public class FlatController {
    private final FlatService flatService;

    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    @GetMapping
    public ResponseEntity<List<Flat>> getAllFlats() {
        return ResponseEntity.ok(flatService.getAllFlats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flat> getFlatById(@PathVariable Long id) {
        return ResponseEntity.ok(flatService.getFlatById(id));
    }

    @PostMapping
    public ResponseEntity<Flat> createFlat(@RequestBody Flat flat) {
        return ResponseEntity.ok(flatService.createFlat(flat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flat> updateFlat(@PathVariable Long id, @RequestBody Flat flat) {
        return ResponseEntity.ok(flatService.updateFlat(id, flat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        try {
            flatService.deleteFlat(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }
} 