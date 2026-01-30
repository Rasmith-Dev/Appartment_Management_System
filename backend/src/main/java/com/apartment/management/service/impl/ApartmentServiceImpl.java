package com.apartment.management.service.impl;

import com.apartment.management.model.Apartment;
import com.apartment.management.repository.ApartmentRepository;
import com.apartment.management.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Override
    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    @Override
    public Optional<Apartment> getApartmentById(Long id) {
        return apartmentRepository.findById(id);
    }

    @Override
    public Apartment createApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    @Override
    public Apartment updateApartment(Long id, Apartment apartment) {
        if (apartmentRepository.existsById(id)) {
            apartment.setId(id);
            return apartmentRepository.save(apartment);
        }
        throw new RuntimeException("Apartment not found with id: " + id);
    }

    @Override
    public void deleteApartment(Long id) {
        if (apartmentRepository.existsById(id)) {
            apartmentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Apartment not found with id: " + id);
        }
    }
} 