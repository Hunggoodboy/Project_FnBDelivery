package com.fnb.backend.repository;

import com.fnb.backend.entity.FavouriteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteRequestRepository extends JpaRepository<FavouriteRequest, Long> {
}
