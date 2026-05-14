package com.fnb.backend.repository;

import com.fnb.backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByPhoneNumber(String phoneNumber);
    Users findByUsername(String username);
}
