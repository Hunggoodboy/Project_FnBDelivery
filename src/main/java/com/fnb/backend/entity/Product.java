package com.fnb.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thymeleaf.context.ILazyContextVariable;

import java.util.List;
import java.util.Set;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private int categoryId;

    private String name;
    @Column(name = "description", length = 2000)
    private String description;
    private String imageUrl;
    private long price, discount;

    public Product(Long id, int categoryId, String name, String description, String imageUrl, long price, long discount) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
    }
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @JoinTable(
            name = "product_ingredient",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients;
    public Product() {}

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Nutrition nutrition;

}
