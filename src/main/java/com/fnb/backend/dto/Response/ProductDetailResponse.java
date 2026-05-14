package com.fnb.backend.dto.Response;
import com.fnb.backend.dto.ProductInfoDto;
import com.fnb.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {
    private ProductInfoDto product;
    private int calories, protein, fat, sugar, fiber, carbohydrate;
    private List<String> ingredients;
    private List<ProductSummaryResponse> relatedProducts;
}
