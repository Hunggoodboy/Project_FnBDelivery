package com.fnb.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // Nên để tên bảng viết thường cho đồng bộ
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với thực thể Users (người đặt hàng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    // Các trường thông tin giao hàng từ form checkout
    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String address;

    @Column(length = 1000)
    private String note;

    // Lưu hình thức trả: eye, lip, cheek, forehead, hug
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "total_price")
    private Long totalPrice;

    // Trạng thái đơn hàng: PENDING (Chờ), SHIPPING (Đang giao), COMPLETED (Đã nhận)
    private String status;

    // Tự động lưu thời gian tạo đơn hàng
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}