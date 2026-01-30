package com.apartment.management.dto;

import lombok.Data;

@Data
public class TenantRequest {
    private Long userId;
    private Long flatId;
    private String leaseStart;
    private String leaseEnd;
    private String phone;
}
