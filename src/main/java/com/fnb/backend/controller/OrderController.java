package com.fnb.backend.controller;

import com.fnb.backend.dto.Request.OrderRequestDTO;
import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.service.OrderService;
import jakarta.persistence.criteria.Order;
import com.fnb.backend.dto.Response.OrderResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(@RequestBody List<OrderRequestDTO> orders){
        try {
            ApiResponse response = orderService.createOrders(orders);
            return ResponseEntity.ok(response);
        }
        catch (RuntimeException e){
            // In ra console để "Anh chin" debug ở IntelliJ
            e.printStackTrace();

            return ResponseEntity.ok(
                    ApiResponse.builder()
                               .success(false)
                               // Trả về câu thông báo lỗi THẬT từ Service ném ra
                               .message("Lỗi: " + e.getMessage())
                               .build());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/today")
    public ResponseEntity<List<OrderResponseDTO>> getTodayOrders() {
        return ResponseEntity.ok(orderService.getTodayOrders());
    }
}
