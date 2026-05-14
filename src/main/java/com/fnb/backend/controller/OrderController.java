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
            System.out.println("Thành công");
            return ResponseEntity.ok(response);
        }
        catch (RuntimeException e){
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(false)
                            .message("Anh chin mời công chúa iu đăng nhập zùi gửi lại đơn giúp anh nhé")
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
