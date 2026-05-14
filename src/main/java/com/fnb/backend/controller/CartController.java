package com.fnb.backend.controller;

import com.fnb.backend.dto.CartItemDTO;
import com.fnb.backend.entity.Cart;
import com.fnb.backend.repository.CartRepository;
import com.fnb.backend.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<?> findCart(HttpSession session) {
        Object UserID = session.getAttribute("UserID");
        if(UserID==null){
            return ResponseEntity.status(401).body("Anh chin mời công chúa Trần Lê Khánh Chi iu của anh đăng nhập đã rui đặt bánh nhéee");
        }
        else{
            long userId = (long) UserID;
            List<CartItemDTO> carts = cartRepository.FindCart(userId);
            return ResponseEntity.ok(carts);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(HttpSession session, @RequestBody java.util.Map<String, Long> payload) {
        Object UserID = session.getAttribute("UserID");
        if(UserID==null){
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập!");
        }
        Long productId = payload.get("productId");
        Long quantity = payload.get("quantity");
        if (productId == null || quantity == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin sản phẩm hoặc số lượng");
        }
        try {
            return ResponseEntity.ok(cartService.addToCart((long) UserID, productId, quantity));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/update/{cartId}")
    public ResponseEntity<?> updateQuantity(HttpSession session, @PathVariable Long cartId, @RequestParam int delta) {
        if(session.getAttribute("UserID") == null) return ResponseEntity.status(401).body("Chưa đăng nhập");
        try {
            cartService.updateQuantity(cartId, delta);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<?> removeFromCart(HttpSession session, @PathVariable Long cartId) {
        if(session.getAttribute("UserID") == null) return ResponseEntity.status(401).body("Chưa đăng nhập");
        try {
            cartService.removeFromCart(cartId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
