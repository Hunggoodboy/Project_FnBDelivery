package com.fnb.backend.repository;

import com.fnb.backend.entity.Nutrition;
import com.fnb.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NutritionRepository extends JpaRepository<Nutrition,Integer> {
    Nutrition findNutritionByProductId(Long productId);
    Optional<Nutrition> findByProduct(Product product);
}
