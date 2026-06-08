package com.campus.lostfound.service;

import com.campus.lostfound.matching.*;
import com.campus.lostfound.model.FoundItem;
import com.campus.lostfound.model.LostItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchingEngineTest {

    @Test
    public void testCategoryMatchingStrategy() {
        CategoryMatchingStrategy strategy = new CategoryMatchingStrategy();
        
        LostItem lost = new LostItem();
        FoundItem found = new FoundItem();

        // Exact match
        lost.setCategory("Phone");
        found.setCategory("Phone");
        assertEquals(1.0, strategy.calculateSimilarity(lost, found), 0.01);

        // Synonym group match
        lost.setCategory("Smartphone");
        found.setCategory("Cell Phone");
        assertEquals(0.7, strategy.calculateSimilarity(lost, found), 0.01);

        // Different category
        lost.setCategory("Wallet");
        found.setCategory("Laptop");
        assertEquals(0.0, strategy.calculateSimilarity(lost, found), 0.01);
    }

    @Test
    public void testColorMatchingStrategy() {
        ColorMatchingStrategy strategy = new ColorMatchingStrategy();
        
        LostItem lost = new LostItem();
        FoundItem found = new FoundItem();

        // Exact match
        lost.setColor("Black");
        found.setColor("Black");
        assertEquals(1.0, strategy.calculateSimilarity(lost, found), 0.01);

        // Shade group match
        lost.setColor("Charcoal");
        found.setColor("Black");
        assertEquals(0.8, strategy.calculateSimilarity(lost, found), 0.01);

        // Mismatch
        lost.setColor("Blue");
        found.setColor("White");
        assertEquals(0.3, strategy.calculateSimilarity(lost, found), 0.01);
    }

    @Test
    public void testBrandMatchingStrategy() {
        BrandMatchingStrategy strategy = new BrandMatchingStrategy();

        LostItem lost = new LostItem();
        FoundItem found = new FoundItem();

        // Exact case insensitive
        lost.setBrand("Apple");
        found.setBrand("apple");
        assertEquals(1.0, strategy.calculateSimilarity(lost, found), 0.01);

        // Containment
        lost.setBrand("Sony Corp");
        found.setBrand("Sony");
        assertEquals(1.0, strategy.calculateSimilarity(lost, found), 0.01);

        // Unspecified
        lost.setBrand("");
        found.setBrand("Apple");
        assertEquals(0.5, strategy.calculateSimilarity(lost, found), 0.01);
    }

    @Test
    public void testLocationMatchingStrategy() {
        LocationMatchingStrategy strategy = new LocationMatchingStrategy();

        LostItem lost = new LostItem();
        FoundItem found = new FoundItem();

        // Close coordinates (under 100m)
        lost.setLocationLat("40.7128");
        lost.setLocationLng("-74.0060");
        found.setLocationLat("40.7127");
        found.setLocationLng("-74.0059");
        assertTrue(strategy.calculateSimilarity(lost, found) >= 0.8);

        // building string match
        lost.setLocationLat(null);
        lost.setLocationLost("Engineering Building Room 204");
        found.setLocationFound("Engineering Gym Lobby");
        assertEquals(0.7, strategy.calculateSimilarity(lost, found), 0.01);
    }
}
