package com.apartment.management.dto;

import lombok.Data;

@Data
public class ComplaintRequest {
    private Long tenantId;
    private Long flatId;
    private String title;
    private String description;
    private String status;
    private String priority;
}
