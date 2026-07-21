package com.aashish.carrental.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record RentalPeriod(LocalDateTime start, LocalDateTime end) {
    public RentalPeriod {
        Objects.requireNonNull(start, "Start must not be null");
        Objects.requireNonNull(end, "End must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End must be after start");
        }
    }
    public static RentalPeriod forDays(LocalDateTime start, int numberOfDays) {
        Objects.requireNonNull(start, "Start must not be null");
        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("Number of days must be greater than zero");
        }
        return new RentalPeriod(start, start.plusDays(numberOfDays));
    }
    public boolean overlaps(RentalPeriod other) {
        Objects.requireNonNull(other, "Other period must not be null");
        return start.isBefore(other.end) && other.start.isBefore(end);
    }
}
