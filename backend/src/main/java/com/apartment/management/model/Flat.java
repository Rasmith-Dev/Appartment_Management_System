package com.apartment.management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flatNumber;
    private Integer floor;
    private Double area;
    private Double rent;

    @Enumerated(EnumType.STRING)
    private FlatType type;

    @Enumerated(EnumType.STRING)
    private FlatStatus status;
}

enum FlatType {
    ONE_BHK, TWO_BHK, THREE_BHK, FOUR_BHK
}

enum FlatStatus {
    VACANT, OCCUPIED, MAINTENANCE
} 