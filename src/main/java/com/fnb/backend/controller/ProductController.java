package com.fnb.backend.controller;

import com.fnb.backend.entity.Product;
import com.fnb.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Product product) {
        try {
            return ResponseEntity.ok(productService.saveProduct(product));
        } catch (Exception e) {
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
