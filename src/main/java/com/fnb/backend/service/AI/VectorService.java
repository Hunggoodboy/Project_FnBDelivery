package com.fnb.backend.service.AI;

import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.entity.Product;
import com.fnb.backend.entity.Nutrition;
import com.fnb.backend.repository.ProductRepository;
import com.fnb.backend.repository.NutritionRepository;
import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate; // Thêm thư viện này
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VectorService {
    private final DocumentService documentService;
    private final ProductRepository productRepository;
    private final NutritionRepository nutritionRepository;
    private final PgVectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate; // Dùng để xóa dữ liệu vector cũ

    public ApiResponse addAllVectorsProducts(){
        // 1. Xóa sạch dữ liệu vector cũ trước khi đồng bộ để tránh trùng lặp
        try {
            jdbcTemplate.execute("TRUNCATE TABLE vector_store");
        } catch (Exception e) {
            System.out.println("Bảng vector_store chưa được tạo hoặc đã trống: " + e.getMessage());
        }

        int pageNumber = 0;
        int pageSize = 100;
        Page<Product> page;
        int totalSynced = 0;

        // 2. Lặp qua tất cả bánh và tạo vector mới
        do {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            page = productRepository.findAll(pageable);
            List<Product> productList = page.getContent();

            if (productList.isEmpty()) break;

            List<Document> documents = new ArrayList<>();
            for (Product product : productList) {
                // Lấy thông tin dinh dưỡng nếu có
                Nutrition nutrition = nutritionRepository.findByProduct(product).orElse(null);
                documents.add(documentService.convertProductToDocument(product, nutrition));
                totalSynced++;
            }

            // Lưu mẻ vector này vào cơ sở dữ liệu
            vectorStore.add(documents);
            pageNumber++;
        } while (page.hasNext());

        return ApiResponse.builder()
                          .success(true)
                          .message("Đã đồng bộ thành công " + totalSynced + " chiếc bánh vào hệ thống AI!")
                          .build();
    }
}