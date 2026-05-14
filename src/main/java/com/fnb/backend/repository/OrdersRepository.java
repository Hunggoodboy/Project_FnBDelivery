package com.fnb.backend.repository;

import com.fnb.backend.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Orders> findAllByOrderByCreatedAtDesc();
}
