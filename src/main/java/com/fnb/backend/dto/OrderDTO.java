package com.fnb.backend.dto;

import lombok.Data;

@Data
public class OrderDTO {
    // Thông tin người mua
    private String customerName;
    private String phoneNumber;
    private String address;
    private String note;
    private String paymentMethod; // "cod" hoặc "banking"

    // Thông tin sản phẩm (để gửi lại backend xử lý đặt hàng)
    private Long productId;
    private Integer quantity;
}