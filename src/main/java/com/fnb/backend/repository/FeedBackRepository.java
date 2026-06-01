package com.fnb.backend.repository;

import com.fnb.backend.entity.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
    List<FeedBack> findByProductIdOrderByCreateTimeDesc(Long productId);
}
