package com.aashish.carrental.domain;

import java.util.Objects;

public record Car(String id, CarType type) {
    public Car {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Car id must not be blank");
        }
        Objects.requireNonNull(type, "Car type must not be null");
    }
}
