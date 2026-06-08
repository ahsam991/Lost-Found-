package com.campus.lostfound.matching;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.FoundItem;
import java.util.List;
import java.util.Map;

public class CategoryMatchingStrategy implements MatchingStrategy {
    private static final Map<String, List<String>> SIMILAR_CATEGORIES = Map.of(
        "phone", List.of("phone", "smartphone", "mobile", "cell phone", "iphone", "android"),
        "laptop", List.of("laptop", "notebook", "macbook", "computer", "pc"),
        "wallet", List.of("wallet", "purse", "money holder", "handbag", "cardholder")
    );

    @Override
    public double calculateSimilarity(LostItem lost, FoundItem found) {
        if (lost.getCategory() == null || found.getCategory() == null) {
            return 0.0;
        }
        
        String lostCat = lost.getCategory().toLowerCase().trim();
        String foundCat = found.getCategory().toLowerCase().trim();

        if (lostCat.equals(foundCat)) {
            return 1.0;
        }

        // Check for similar category groups
        for (Map.Entry<String, List<String>> entry : SIMILAR_CATEGORIES.entrySet()) {
            List<String> list = entry.getValue();
            if (list.contains(lostCat) && list.contains(foundCat)) {
                return 0.7;
            }
        }

        return 0.0;
    }
}
