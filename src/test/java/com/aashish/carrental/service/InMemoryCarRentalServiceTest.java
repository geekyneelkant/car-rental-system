package com.aashish.carrental.service;

import com.aashish.carrental.domain.Car;
import com.aashish.carrental.domain.CarType;
import com.aashish.carrental.domain.Reservation;
import com.aashish.carrental.service.exception.CarNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCarRentalServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 10, 10, 0);

    private InMemoryCarRentalService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryCarRentalService(List.of(
                new Car("SEDAN-1", CarType.SEDAN),
                new Car("SEDAN-2", CarType.SEDAN),
                new Car("SUV-1", CarType.SUV),
                new Car("VAN-1", CarType.VAN)
        ));
    }

    @Test
    void reservesRequestedCarTypeForRequestedNumberOfDays() {
        Reservation reservation = service.reserve(CarType.SUV, START, 3);

        assertEquals(CarType.SUV, reservation.car().type());
        assertEquals(START, reservation.period().start());
        assertEquals(START.plusDays(3), reservation.period().end());
    }

    @Test
    void allocatesDifferentCarsWhenReservationsOverlap() {
        Reservation first = service.reserve(CarType.SEDAN, START, 2);
        Reservation second = service.reserve(CarType.SEDAN, START.plusHours(1), 1);

        assertNotEquals(first.car(), second.car());
    }

    @Test
    void rejectsReservationWhenAllCarsOfTypeAreOccupied() {
        service.reserve(CarType.SEDAN, START, 2);
        service.reserve(CarType.SEDAN, START, 2);

        assertThrows(CarNotAvailableException.class,
                () -> service.reserve(CarType.SEDAN, START.plusHours(1), 1));
    }

    @Test
    void allowsSameCarToBeReservedForNonOverlappingPeriod() {
        Reservation first = service.reserve(CarType.VAN, START, 2);
        Reservation second = service.reserve(CarType.VAN, START.plusDays(2), 1);

        assertEquals(first.car(), second.car());
    }

    @Test
    void shouldAllowBackToBackReservationsForSameCar() {
        InMemoryCarRentalService oneCarService = new InMemoryCarRentalService(
                List.of(new Car("SUV-1", CarType.SUV)));

        Reservation firstReservation =
                oneCarService.reserve(CarType.SUV, START, 3);

        Reservation secondReservation = oneCarService.reserve(CarType.SUV, START.plusDays(3), 2);

        assertAll(
                () -> assertEquals(firstReservation.period().end(), secondReservation.period().start()),
                () -> assertEquals(firstReservation.car().id(), secondReservation.car().id())
        );
    }

    @Test
    void shouldRejectReservationThatPartiallyOverlapsExistingReservation() {
        InMemoryCarRentalService oneCarService = new InMemoryCarRentalService(
                List.of(new Car("SUV-1", CarType.SUV)));

        oneCarService.reserve(CarType.SUV, START, 3);

        /*
         * Existing reservation: START -------- START + 3 days
         * New reservation: START + 2 days -------- START + 4 days
         */
        assertThrows(CarNotAvailableException.class, () -> oneCarService.reserve(
                CarType.SUV, START.plusDays(2), 2));
    }

    @Test
    void shouldRejectReservationThatFullyContainsExistingReservation() {
        InMemoryCarRentalService oneCarService = new InMemoryCarRentalService(
                List.of(new Car("SUV-1", CarType.SUV)));

        /*
         * Existing reservation:
         *       START + 1 day ---- START + 3 days
         */
        oneCarService.reserve(CarType.SUV, START.plusDays(1), 2);

        /*
         * New reservation:
         * START --------------------------- START + 5 days
         */
        assertThrows(CarNotAvailableException.class, () -> oneCarService.reserve(
                CarType.SUV, START, 5));
    }

    @Test
    void inventoryIsIndependentForEachCarType() {
        service.reserve(CarType.SUV, START, 2);

        Reservation van = service.reserve(CarType.VAN, START, 2);

        assertEquals(CarType.VAN, van.car().type());
    }

    @Test
    void allowsSamePeriodForDifferentCarTypes() {
        Reservation suv =
                service.reserve(CarType.SUV, START, 2);

        Reservation sedan =
                service.reserve(CarType.SEDAN, START, 2);

        Reservation van =
                service.reserve(CarType.VAN, START, 2);

        assertEquals(CarType.SUV, suv.car().type());
        assertEquals(CarType.SEDAN, sedan.car().type());
        assertEquals(CarType.VAN, van.car().type());
    }


    @Test
    void rejectsSecondOverlappingReservationWhenOnlyOneCarExists() {
        InMemoryCarRentalService oneSuvService = new InMemoryCarRentalService(List.of(
                new Car("SUV-1", CarType.SUV)
        ));

        oneSuvService.reserve(CarType.SUV, START, 3);

        assertThrows(
                CarNotAvailableException.class,
                () -> oneSuvService.reserve(
                        CarType.SUV,
                        START.plusDays(1),
                        1
                )
        );
    }

    @Test
    void rejectsInvalidRequests() {
        assertThrows(NullPointerException.class, () -> service.reserve(null, START, 1));
        assertThrows(NullPointerException.class, () -> service.reserve(CarType.SUV, null, 1));
        assertThrows(IllegalArgumentException.class, () -> service.reserve(CarType.SUV, START, 0));
    }
}
