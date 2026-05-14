package com.fnb.backend.service;


import com.fnb.backend.dto.ProductInfoDto;
import com.fnb.backend.dto.Response.ProductDetailResponse;
import com.fnb.backend.dto.Response.ProductSummaryResponse;
import com.fnb.backend.entity.Nutrition;
import com.fnb.backend.entity.Product;
import com.fnb.backend.repository.IngredientRepository;
import com.fnb.backend.repository.NutritionRepository;
import com.fnb.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NutritionRepository nutritionRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public List<ProductSummaryResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(p -> {
            Nutrition n = nutritionRepository.findNutritionByProductId(p.getId());
            return new ProductSummaryResponse(
                    p.getId(),
                    p.getName(),
                    p.getImageUrl(),
                    p.getDescription(),
                    (n != null) ? n.getCalories() : 0,
                    p.getPrice(),
                    p.getDiscount()
            );
        }).collect(Collectors.toList());
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public ProductDetailResponse getProductDetail(Long id) {
        Product product = productRepository.findById(id).orElse(null);

        // Tránh lỗi NullPointerException nếu tìm không thấy sản phẩm
        if (product == null) {
            return null;
        }

        Nutrition n = nutritionRepository.findNutritionByProductId(id);
        List<String> ingredients = ingredientRepository.findIngredientNameByProductId(id);

        List<ProductSummaryResponse> relatedProducts = productRepository.findAll().stream()
                                                                        .map(p -> {
                                                                            Nutrition rn = nutritionRepository.findNutritionByProductId(p.getId());
                                                                            return new ProductSummaryResponse(
                                                                                    p.getId(),
                                                                                    p.getName(),
                                                                                    p.getImageUrl(),
                                                                                    p.getDescription(),
                                                                                    (rn != null) ? rn.getCalories() : 0,
                                                                                    p.getPrice(),
                                                                                    p.getDiscount()
                                                                            );
                                                                        }).collect(Collectors.toList());

        // Đổ dữ liệu từ Entity sang DTO sạch
        ProductInfoDto cleanProductInfo = new ProductInfoDto(
                product.getId(),
                product.getCategoryId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getDiscount()
        );

        return new ProductDetailResponse(
                cleanProductInfo, // Trả về DTO thay vì Entity
                (n != null) ? n.getCalories() : 0,
                (n != null) ? n.getProtein() : 0,
                (n != null) ? n.getFat() : 0,
                (n != null) ? n.getSugar() : 0,
                (n != null) ? n.getFiber() : 0,
                (n != null) ? n.getCarbohydrate() : 0,
                ingredients,
                relatedProducts
        );
    }
}