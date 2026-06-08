package com.campus.lostfound.matching;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.FoundItem;

public class LocationMatchingStrategy implements MatchingStrategy {
    @Override
    public double calculateSimilarity(LostItem lost, FoundItem found) {
        // Check GPS coordinates first
        if (lost.getLocationLat() != null && lost.getLocationLng() != null &&
            found.getLocationLat() != null && found.getLocationLng() != null) {
            try {
                double lat1 = Double.parseDouble(lost.getLocationLat());
                double lon1 = Double.parseDouble(lost.getLocationLng());
                double lat2 = Double.parseDouble(found.getLocationLat());
                double lon2 = Double.parseDouble(found.getLocationLng());

                double distance = haversineDistance(lat1, lon1, lat2, lon2); // in meters
                if (distance <= 100) return 1.0;
                if (distance <= 300) return 0.8;
                if (distance <= 1000) return 0.5;
                return 0.1;
            } catch (NumberFormatException e) {
                // Coordinates are invalid strings, fallback to string matching
            }
        }

        // String matching fallback
        if (lost.getLocationLost() == null || found.getLocationFound() == null) {
            return 0.2;
        }

        String locLost = lost.getLocationLost().toLowerCase().trim();
        String locFound = found.getLocationFound().toLowerCase().trim();

        if (locLost.equals(locFound) || locLost.contains(locFound) || locFound.contains(locLost)) {
            return 0.9;
        }

        // check common campus building substring
        String[] buildings = {"library", "engineering", "union", "gym", "quad", "dorms", "hall"};
        for (String b : buildings) {
            if (locLost.contains(b) && locFound.contains(b)) {
                return 0.7;
            }
        }

        return 0.2;
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
