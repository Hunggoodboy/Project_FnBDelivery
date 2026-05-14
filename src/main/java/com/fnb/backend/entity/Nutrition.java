package com.fnb.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Nutrition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;
    private int Calories, Protein, Fat, Sugar,Fiber,Carbohydrate;
    public  Nutrition(){}
    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private Product product;
}
