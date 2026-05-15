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

    @org.springframework.transaction.annotation.Transactional
    public Product saveFullProduct(Product product, Nutrition nutrition, List<String> ingredientNames) {
        // 1. Save Product first to get ID
        Product savedProduct = productRepository.save(product);

        // 2. Handle Nutrition
        if (nutrition != null) {
            nutrition.setProduct(savedProduct);
            nutritionRepository.save(nutrition);
            savedProduct.setNutrition(nutrition);
        }

        // 3. Handle Ingredients
        if (ingredientNames != null && !ingredientNames.isEmpty()) {
            java.util.Set<com.fnb.backend.entity.Ingredient> ingredients = ingredientNames.stream()
                .map(name -> name.trim())
                .filter(name -> !name.isEmpty())
                .map(name -> {
                    return ingredientRepository.findByName(name)
                        .orElseGet(() -> {
                            com.fnb.backend.entity.Ingredient newIng = new com.fnb.backend.entity.Ingredient();
                            newIng.setName(name);
                            return ingredientRepository.save(newIng);
                        });
                })
                .collect(java.util.stream.Collectors.toSet());
            savedProduct.setIngredients(ingredients);
            productRepository.save(savedProduct);
        }

        return savedProduct;
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