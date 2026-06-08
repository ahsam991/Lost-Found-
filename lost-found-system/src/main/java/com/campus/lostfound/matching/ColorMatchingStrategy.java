package com.campus.lostfound.matching;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.FoundItem;
import java.util.List;
import java.util.Map;

public class ColorMatchingStrategy implements MatchingStrategy {
    private static final Map<String, List<String>> COLOR_GROUPS = Map.of(
        "black", List.of("black", "dark", "charcoal", "grey", "gray"),
        "white", List.of("white", "silver", "light", "gold"),
        "blue", List.of("blue", "navy", "cyan", "teal")
    );

    @Override
    public double calculateSimilarity(LostItem lost, FoundItem found) {
        if (lost.getColor() == null || found.getColor() == null) {
            return 0.3; // Default low match for unspecified colors
        }
        
        String lostColor = lost.getColor().toLowerCase().trim();
        String foundColor = found.getColor().toLowerCase().trim();

        if (lostColor.equals(foundColor)) {
            return 1.0;
        }

        for (Map.Entry<String, List<String>> entry : COLOR_GROUPS.entrySet()) {
            List<String> list = entry.getValue();
            if (list.contains(lostColor) && list.contains(foundColor)) {
                return 0.8;
            }
        }

        return 0.3;
    }
}
