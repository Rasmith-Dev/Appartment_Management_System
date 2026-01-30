package com.apartment.management.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long tenantId;
    private Long flatId;
    private Double amount;
    private String type;
    private String status;
    private String dueDate;
    private String description;
}
