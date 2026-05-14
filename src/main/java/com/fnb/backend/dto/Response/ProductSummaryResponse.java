package com.fnb.backend.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryResponse {
    private Long id;
    private String name;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    private String description;
    
    @JsonProperty("calorie")
    private int calories;
    
    private long price;
    private long discount;
}
