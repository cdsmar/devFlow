package com.example.devFlow.offer;

import com.example.devFlow.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    public OfferService(OfferRepository offerRepository, UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    public List<Offer> getOffers() {
        return offerRepository.findAll();
    }

    public Offer createOffer(OfferRequest request) {
        Offer offer = new Offer();

        offer.setDescription(request.description());
        offer.setFileName(request.fileName());

        return offerRepository.save(offer);
    }
}
