package com.campus.lostfound.matching;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.FoundItem;

public class BrandMatchingStrategy implements MatchingStrategy {
    @Override
    public double calculateSimilarity(LostItem lost, FoundItem found) {
        if (lost.getBrand() == null || found.getBrand() == null ||
            lost.getBrand().trim().isEmpty() || found.getBrand().trim().isEmpty()) {
            return 0.5; // Neutral score when brand is unspecified
        }

        String lostBrand = lost.getBrand().toLowerCase().trim();
        String foundBrand = found.getBrand().toLowerCase().trim();

        if (lostBrand.equals(foundBrand) || lostBrand.contains(foundBrand) || foundBrand.contains(lostBrand)) {
            return 1.0;
        }

        return 0.0;
    }
}
