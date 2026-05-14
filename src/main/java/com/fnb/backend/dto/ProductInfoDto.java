package com.fnb.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDto {
    private Long id;
    private int categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private long price;
    private long discount;
}