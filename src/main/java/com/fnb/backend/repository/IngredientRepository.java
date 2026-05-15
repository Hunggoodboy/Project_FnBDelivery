package com.fnb.backend.repository;

import com.fnb.backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Query(value = "SELECT a.name FROM ingredient a " +
            "JOIN product_ingredient b ON a.id = b.ingredient_id " +
            "WHERE b.product_id = :productId", nativeQuery = true)
    List<String> findIngredientNameByProductId(@Param("productId") long productId);

    java.util.Optional<Ingredient> findByName(String name);
}
