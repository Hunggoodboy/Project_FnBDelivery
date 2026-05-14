package com.fnb.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long cartId;
    private String productName;
    private String productImage;
    private long price;
    private long discount;
    private Long quantity;
    private Long totalPrice;
}