package com.example.devFlow.offer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByProjectIdIn(List<Long> projectIds);

}

