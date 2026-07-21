package com.aashish.carrental.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalPeriodTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 10, 10, 0);

    @Test
    void overlappingPeriodsAreDetected() {
        RentalPeriod first = RentalPeriod.forDays(START, 2);
        RentalPeriod second = RentalPeriod.forDays(START.plusDays(1), 2);

        assertTrue(first.overlaps(second));
        assertTrue(second.overlaps(first));
    }

    @Test
    void adjacentPeriodsDoNotOverlap() {
        RentalPeriod first = RentalPeriod.forDays(START, 2);
        RentalPeriod second = RentalPeriod.forDays(START.plusDays(2), 1);

        assertFalse(first.overlaps(second));
    }

    @Test
    void numberOfDaysMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> RentalPeriod.forDays(START, 0));
        assertThrows(IllegalArgumentException.class, () -> RentalPeriod.forDays(START, -1));
    }
}
