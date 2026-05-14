package com.fnb.backend.repository;

import com.fnb.backend.dto.CartItemDTO;
import com.fnb.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Sử dụng Constructor Expression để mapping trực tiếp dữ liệu vào DTO
    @Query("SELECT new com.fnb.backend.dto.CartItemDTO(a.cart_id, b.name, b.imageUrl, b.price, b.discount, a.quantity, a.total_price) " +
            "FROM Cart a JOIN Product b ON a.product_id = b.id " +
            "WHERE a.users.userId = :user_id")
    List<CartItemDTO> FindCart(@Param("user_id") Long user_id);

    @Query("SELECT c FROM Cart c WHERE c.users.userId = :userId AND c.product_id = :productId")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}