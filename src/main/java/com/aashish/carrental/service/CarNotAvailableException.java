package com.aashish.carrental.service;

import com.aashish.carrental.domain.CarType;
import com.aashish.carrental.domain.RentalPeriod;

public final class CarNotAvailableException extends RuntimeException {

    public CarNotAvailableException(CarType type, RentalPeriod period) {
        super("No " + type + " is available from " + period.start() + " to " + period.end());
    }
}
