package com.campus.lostfound.matching;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.FoundItem;

public interface MatchingStrategy {
    double calculateSimilarity(LostItem lost, FoundItem found);
}
