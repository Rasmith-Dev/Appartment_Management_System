package com.apartment.management.service;

import com.apartment.management.model.Apartment;
import java.util.List;
import java.util.Optional;

public interface ApartmentService {
    List<Apartment> getAllApartments();
    Optional<Apartment> getApartmentById(Long id);
    Apartment createApartment(Apartment apartment);
    Apartment updateApartment(Long id, Apartment apartment);
    void deleteApartment(Long id);
} 