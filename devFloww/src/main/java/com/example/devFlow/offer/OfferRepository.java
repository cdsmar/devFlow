package com.example.devFlow.offer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByProjectIdIn(List<Long> projectIds);
    List<Offer> findByProjectIdInAndStatusIn(List<Long> projectIds, List<Offer.OfferStatus> statuses);
    List<Offer> findByUserIdAndStatusIn(Long userId, List<Offer.OfferStatus> statuses);
    List<Offer> findByUserIdAndStatus(Long userId, Offer.OfferStatus status);
    List<Offer> findByUserId(Long userId);



}

