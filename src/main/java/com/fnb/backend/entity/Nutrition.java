package com.fnb.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
public class Nutrition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private int calories, protein, fat, sugar, fiber, carbohydrate;
    public  Nutrition(){}
    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    @EqualsAndHashCode.Exclude
    private Product product;
}
