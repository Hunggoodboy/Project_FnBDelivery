package com.fnb.backend.controller;

import com.fnb.backend.entity.Product;
import com.fnb.backend.entity.Nutrition;
import com.fnb.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try{
            return ResponseEntity.ok(productService.getAllProducts());
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") long price,
            @RequestParam("discount") long discount,
            @RequestParam("categoryId") int categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "calories", defaultValue = "0") int calories,
            @RequestParam(value = "protein", defaultValue = "0") int protein,
            @RequestParam(value = "fat", defaultValue = "0") int fat,
            @RequestParam(value = "sugar", defaultValue = "0") int sugar,
            @RequestParam(value = "fiber", defaultValue = "0") int fiber,
            @RequestParam(value = "carbohydrate", defaultValue = "0") int carbohydrate,
            @RequestParam(value = "ingredients", required = false) String ingredientsStr) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setDiscount(discount);
            product.setCategoryId(categoryId);

            // Handle Image Upload
            if (image != null && !image.isEmpty()) {
                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                String uploadDir = "src/main/resources/static/assets/img/";
                Path uploadPath = Paths.get(uploadDir);
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = image.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    product.setImageUrl("/assets/img/" + fileName);
                } catch (IOException ioe) {
                    throw new IOException("Could not save image file: " + fileName, ioe);
                }
            }

            // Handle Nutrition
            Nutrition nutrition = new Nutrition();
            nutrition.setCalories(calories);
            nutrition.setProtein(protein);
            nutrition.setFat(fat);
            nutrition.setSugar(sugar);
            nutrition.setFiber(fiber);
            nutrition.setCarbohydrate(carbohydrate);

            // Handle Ingredients
            List<String> ingredientList = null;
            if (ingredientsStr != null && !ingredientsStr.isEmpty()) {
                ingredientList = Arrays.asList(ingredientsStr.split(","));
            }

            return ResponseEntity.ok(productService.saveFullProduct(product, nutrition, ingredientList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Lỗi lưu sản phẩm: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> productDetail(@PathVariable Long id){
        try {
            return ResponseEntity.ok(productService.getProductDetail(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm với id: " + id);
        }
    }
}
