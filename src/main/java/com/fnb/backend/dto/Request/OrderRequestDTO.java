package com.fnb.backend.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    // Thông tin nhận bánh
    private String customerName;
    private String phoneNumber;
    private String nameOfFood;
    private String quantity;
    private String address;
    private String note;

    // Hình thức "trả bằng tình cảm" (eye, lip, cheek, forehead, hug)
    private String paymentMethod;

    // Tổng số cái ôm và chơm (để lưu vào database)
    private Long totalPrice;
}