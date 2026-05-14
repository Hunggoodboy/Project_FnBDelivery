package com.fnb.backend.service.AI;

import com.fnb.backend.entity.Product;
import com.fnb.backend.entity.Nutrition;
import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DocumentService {

    public Document convertProductToDocument(Product product, Nutrition nutrition){
        // Xử lý thông tin dinh dưỡng (nếu chưa có thì mặc định là 0 để không bị lỗi)
        String calories = nutrition != null ? String.valueOf(nutrition.getCalories()) : "0";
        String protein = nutrition != null ? String.valueOf(nutrition.getProtein()) : "0";
        String fat = nutrition != null ? String.valueOf(nutrition.getFat()) : "0";
        String sugar = nutrition != null ? String.valueOf(nutrition.getSugar()) : "0";
        String fiber = nutrition != null ? String.valueOf(nutrition.getFiber()) : "0";
        String carbohydrate = nutrition != null ? String.valueOf(nutrition.getCarbohydrate()) : "0";

        // Xử lý hương vị / mô tả
        String description = (product.getDescription() != null && !product.getDescription().isEmpty())
                ? product.getDescription()
                : "Bánh nướng thơm ngon, tốt cho sức khỏe.";

        return Document.builder()
                       // 1. LƯU VÀO METADATA (Giúp AI lọc dữ liệu nhanh hơn)
                       .metadata("productId", product.getId().toString())
                       .metadata("name", product.getName())
                       .metadata("price", String.valueOf(product.getPrice()))
                       .metadata("calories", calories)
                       .metadata("protein", protein)
                       .metadata("fat", fat)
                       .metadata("sugar", sugar)
                       .metadata("fiber", fiber)
                       .metadata("flavor", description) // Lưu vị bánh vào metadata

                       // 2. LƯU VÀO TEXT CONTENT (Giúp AI đọc hiểu và tư vấn tự nhiên)
                       .text(String.format("""
                        Tên sản phẩm: %s
                        Hương vị / Mô tả: %s
                        Giá : %s cái ôm
                        Thông tin dinh dưỡng chi tiết (trên 100g): 
                        - Năng lượng: %s Calories
                        - Đạm (Protein): %s g
                        - Chất béo (Fat): %s g
                        - Đường (Sugar): %s g
                        - Chất xơ (Fiber): %s g
                        - Tinh bột (Carbohydrate): %s g
                        """,
                               product.getName(),
                               description,
                               product.getPrice(),
                               calories, protein, fat, sugar, fiber, carbohydrate))
                       .build();
    }
}