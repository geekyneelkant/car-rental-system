package com.aashish.carrental.service;

import com.aashish.carrental.domain.CarType;
import com.aashish.carrental.domain.Reservation;

import java.time.LocalDateTime;

public interface CarRentalService {

    Reservation reserve(CarType carType, LocalDateTime start, int numberOfDays);
}
