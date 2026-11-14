package com.example.dispatch.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HaversineCalculatorTest {

    @Test
    void samePointIsZero() {
        assertEquals(0.0, HaversineCalculator.distanceKm(28.6139, 77.2090, 28.6139, 77.2090), 1e-9);
    }

    @Test
    void bangaloreToChennaiIsAbout290Km() {
        double d = HaversineCalculator.distanceKm(12.9716, 77.5946, 13.0827, 80.2707);
        assertEquals(290.0, d, 5.0);
    }

    @Test
    void distanceIsSymmetric() {
        double a = HaversineCalculator.distanceKm(28.6139, 77.2090, 28.5355, 77.3910);
        double b = HaversineCalculator.distanceKm(28.5355, 77.3910, 28.6139, 77.2090);
        assertEquals(a, b, 1e-9);
    }
}