package com.aashish.carrental.service;

import com.aashish.carrental.domain.Car;
import com.aashish.carrental.domain.CarType;
import com.aashish.carrental.domain.RentalPeriod;
import com.aashish.carrental.domain.Reservation;
import com.aashish.carrental.service.exception.CarNotAvailableException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class InMemoryCarRentalService implements CarRentalService {

    private final List<Car> carList;
    private final List<Reservation> reservations = new ArrayList<>();

    public InMemoryCarRentalService(List<Car> cars) {
        Objects.requireNonNull(cars, "Fleet must not be null");
        if (cars.isEmpty()) {
            throw new IllegalArgumentException("Fleet must not be empty");
        }
        if (cars.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Fleet must not contain null cars");
        }
        if (cars.stream().map(Car::id).distinct().count() != cars.size()) {
            throw new IllegalArgumentException("Car ids must be unique");
        }
        this.carList = List.copyOf(cars);
    }

    @Override
    public synchronized Reservation reserve(CarType carType, LocalDateTime start, int numberOfDays) {
        Objects.requireNonNull(carType, "Car type must not be null");
        RentalPeriod requestedPeriod = RentalPeriod.forDays(start, numberOfDays);

        Car availableCar = this.carList.stream()
                .filter(car -> car.type() == carType)
                .filter(car -> isAvailable(car, requestedPeriod))
                .findFirst()
                .orElseThrow(() -> new CarNotAvailableException(carType, requestedPeriod));

        Reservation reservation = new Reservation(UUID.randomUUID(), availableCar, requestedPeriod);
        reservations.add(reservation);
        return reservation;
    }

    private boolean isAvailable(Car car, RentalPeriod requestedPeriod) {
        return reservations.stream()
                .filter(reservation -> reservation.car().equals(car))
                .noneMatch(reservation -> reservation.period().overlaps(requestedPeriod));
    }
}
