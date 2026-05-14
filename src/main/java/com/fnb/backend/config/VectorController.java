package com.fnb.backend.controller;

import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.service.AI.VectorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vector")
@AllArgsConstructor
public class VectorController {

    private final VectorService vectorService;

    // API để kích hoạt việc đồng bộ toàn bộ dữ liệu bánh vào Vector Database
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse> syncVectors() {
        try {
            ApiResponse response = vectorService.addAllVectorsProducts();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.builder()
                               .success(false)
                               .message("Lỗi khi đồng bộ vector: " + e.getMessage())
                               .build()
            );
        }
    }
}