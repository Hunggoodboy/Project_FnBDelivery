package com.fnb.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

// Đã xóa dòng import org.apache.catalina.User; bị sai

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false)
    private Long cart_id;

    private Long product_id;
    private Long quantity;
    private Long total_price;

    private String address;
    private String phone_number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonIgnore
    private Users users;
}