package com.fnb.backend.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String customerName;
    private String phoneNumber;
    private String nameOfFood;
    private String quantity;
    private String address;
    private String note;
    private String paymentMethod;
    private Long totalPrice;
    private String status;
    private LocalDateTime createdAt;
}
