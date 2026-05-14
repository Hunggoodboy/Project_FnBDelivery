package com.fnb.backend.service;

import com.fnb.backend.entity.Cart;
import com.fnb.backend.entity.Product;
import com.fnb.backend.entity.Users;
import com.fnb.backend.repository.CartRepository;
import com.fnb.backend.repository.ProductRepository;
import com.fnb.backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart addToCart(Long userId, Long productId, Long quantity) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Cart existingItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            if (existingItem.getQuantity() < 1) existingItem.setQuantity(1L);
            existingItem.setTotal_price(existingItem.getQuantity() * (product.getPrice() - (product.getPrice() * product.getDiscount() / 100)));
            return cartRepository.save(existingItem);
        } else {
            Cart newItem = new Cart();
            newItem.setUsers(user);
            newItem.setProduct_id(productId);
            newItem.setQuantity(quantity);
            newItem.setTotal_price(quantity * (product.getPrice() - (product.getPrice() * product.getDiscount() / 100)));
            return cartRepository.save(newItem);
        }
    }

    public void updateQuantity(Long cartId, int delta) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart item not found"));
        Product product = productRepository.findById(cart.getProduct_id()).orElseThrow(() -> new RuntimeException("Product not found"));
        
        cart.setQuantity(cart.getQuantity() + delta);
        if (cart.getQuantity() < 1) {
            cartRepository.delete(cart);
        } else {
            cart.setTotal_price(cart.getQuantity() * (product.getPrice() - (product.getPrice() * product.getDiscount() / 100)));
            cartRepository.save(cart);
        }
    }

    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}
