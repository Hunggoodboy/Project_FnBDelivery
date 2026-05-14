package com.fnb.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data

public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long userId;
    private String username, email, password, role;
    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cart> carts;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Orders> orders;

    public Users(String username, String email, String password, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public Users() {

    }
}
