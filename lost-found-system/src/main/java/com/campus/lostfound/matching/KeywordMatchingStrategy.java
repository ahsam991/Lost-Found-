package com.campus.lostfound.matching;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.FoundItem;
import java.util.HashSet;
import java.util.Set;

public class KeywordMatchingStrategy implements MatchingStrategy {
    @Override
    public double calculateSimilarity(LostItem lost, FoundItem found) {
        String lostText = (lost.getTitle() + " " + lost.getDescription()).toLowerCase();
        String foundText = (found.getTitle() + " " + found.getDescription()).toLowerCase();

        Set<String> lostWords = tokenize(lostText);
        Set<String> foundWords = tokenize(foundText);

        if (lostWords.isEmpty() || foundWords.isEmpty()) {
            return 0.0;
        }

        int intersectionSize = 0;
        for (String word : lostWords) {
            if (foundWords.contains(word)) {
                intersectionSize++;
            }
        }

        // Jaccard similarity coefficient: intersection / union
        int unionSize = lostWords.size() + foundWords.size() - intersectionSize;
        return (double) intersectionSize / unionSize;
    }

    private Set<String> tokenize(String text) {
        Set<String> set = new HashSet<>();
        // Split by non-word characters
        String[] tokens = text.split("[^a-zA-Z0-9]+");
        for (String t : tokens) {
            // Filter out common stop-words and short tokens
            if (t.length() > 3 && !isStopWord(t)) {
                set.add(t);
            }
        }
        return set;
    }

    private boolean isStopWord(String word) {
        return word.equals("with") || word.equals("that") || word.equals("this") || 
               word.equals("from") || word.equals("have") || word.equals("your") || 
               word.equals("here") || word.equals("some") || word.equals("black") || 
               word.equals("white") || word.equals("color") || word.equals("brand");
    }
}
