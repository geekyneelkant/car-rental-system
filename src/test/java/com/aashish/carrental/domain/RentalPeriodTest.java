package com.aashish.carrental.domain;

import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldDetectPartiallyOverlappingPeriods() {
        RentalPeriod first =
                RentalPeriod.forDays(START, 2);

        RentalPeriod second =
                RentalPeriod.forDays(START.plusDays(1), 2);

        assertAll(
                () -> assertTrue(first.overlaps(second)),
                () -> assertTrue(second.overlaps(first))
        );
    }

    @Test
    void shouldDetectOverlapWhenPeriodsAreExactlyTheSame() {
        RentalPeriod first =
                RentalPeriod.forDays(START, 2);

        RentalPeriod second =
                RentalPeriod.forDays(START, 2);

        assertAll(
                () -> assertTrue(first.overlaps(second)),
                () -> assertTrue(second.overlaps(first))
        );
    }

    @Test
    void shouldDetectOverlapWhenPeriodsHaveSameStartButDifferentEnds() {
        RentalPeriod shorter =
                RentalPeriod.forDays(START, 1);

        RentalPeriod longer =
                RentalPeriod.forDays(START, 3);

        assertAll(
                () -> assertTrue(shorter.overlaps(longer)),
                () -> assertTrue(longer.overlaps(shorter))
        );
    }

    @Test
    void shouldDetectOverlapWhenPeriodsHaveSameEndButDifferentStarts() {
        RentalPeriod first =
                RentalPeriod.forDays(START, 3);

        RentalPeriod second =
                RentalPeriod.forDays(START.plusDays(1), 2);

        assertEquals(first.end(), second.end());

        assertAll(
                () -> assertTrue(first.overlaps(second)),
                () -> assertTrue(second.overlaps(first))
        );
    }

    @Test
    void shouldDetectOverlapWhenFirstPeriodContainsSecondPeriod() {
        RentalPeriod outer =
                RentalPeriod.forDays(START, 5);

        RentalPeriod inner =
                RentalPeriod.forDays(START.plusDays(1), 2);

        assertAll(
                () -> assertTrue(outer.overlaps(inner)),
                () -> assertTrue(inner.overlaps(outer))
        );
    }

    @Test
    void shouldDetectOverlapWhenSecondPeriodContainsFirstPeriod() {
        RentalPeriod inner =
                RentalPeriod.forDays(START.plusDays(1), 2);

        RentalPeriod outer =
                RentalPeriod.forDays(START, 5);

        assertAll(
                () -> assertTrue(inner.overlaps(outer)),
                () -> assertTrue(outer.overlaps(inner))
        );
    }

    @Test
    void shouldDetectOverlapWhenSecondStartsInsideFirst() {
        RentalPeriod first =
                RentalPeriod.forDays(START, 3);

        RentalPeriod second =
                RentalPeriod.forDays(START.plusDays(2), 3);

        assertAll(
                () -> assertTrue(first.overlaps(second)),
                () -> assertTrue(second.overlaps(first))
        );
    }

    @Test
    void shouldDetectOverlapWhenSecondEndsInsideFirst() {
        RentalPeriod first =
                RentalPeriod.forDays(START.plusDays(2), 3);

        RentalPeriod second =
                RentalPeriod.forDays(START, 3);

        assertAll(
                () -> assertTrue(first.overlaps(second)),
                () -> assertTrue(second.overlaps(first))
        );
    }

    @Test
    void shouldDetectOverlapOneNanosecondBeforePeriodEnds() {
        RentalPeriod first =
                RentalPeriod.forDays(START, 2);

        LocalDateTime justBeforeFirstPeriodEnds =
                first.end().minusNanos(1);

        RentalPeriod second =
                RentalPeriod.forDays(
                        justBeforeFirstPeriodEnds,
                        1
                );

        assertAll(
                () -> assertTrue(first.overlaps(second)),
                () -> assertTrue(second.overlaps(first))
        );
    }

    @Test
    void shouldOverlapWithItself() {
        RentalPeriod period =
                RentalPeriod.forDays(START, 2);

        assertTrue(period.overlaps(period));
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

    @Test
    void shouldNotOverlapWhenSecondStartsExactlyWhenFirstEnds() {
        RentalPeriod first =
                RentalPeriod.forDays(START, 2);

        RentalPeriod second =
                RentalPeriod.forDays(first.end(), 1);

        assertAll(
                () -> assertFalse(first.overlaps(second)),
                () -> assertFalse(second.overlaps(first))
        );
    }

    @Test
    void shouldNotOverlapWhenFirstStartsExactlyWhenSecondEnds() {
        RentalPeriod second =
                RentalPeriod.forDays(START, 2);

        RentalPeriod first =
                RentalPeriod.forDays(
                        START.minusDays(1),
                        1
                );

        assertEquals(first.end(), second.start());

        assertAll(
                () -> assertFalse(first.overlaps(second)),
                () -> assertFalse(second.overlaps(first))
        );
    }

    @Test
    void shouldNotOverlapWhenPeriodsHaveGapBetweenThem() {
        RentalPeriod first =
                RentalPeriod.forDays(START, 2);

        RentalPeriod second =
                RentalPeriod.forDays(START.plusDays(4), 2);

        assertAll(
                () -> assertFalse(first.overlaps(second)),
                () -> assertFalse(second.overlaps(first))
        );
    }

    @Test
    void shouldNotOverlapWhenOtherPeriodIsCompletelyBefore() {
        RentalPeriod earlier =
                RentalPeriod.forDays(START.minusDays(5), 2);

        RentalPeriod later =
                RentalPeriod.forDays(START, 2);

        assertAll(
                () -> assertFalse(earlier.overlaps(later)),
                () -> assertFalse(later.overlaps(earlier))
        );
    }

    @Test
    void shouldNotOverlapWhenOtherPeriodIsCompletelyAfter() {
        RentalPeriod earlier =
                RentalPeriod.forDays(START, 2);

        RentalPeriod later =
                RentalPeriod.forDays(START.plusDays(5), 2);

        assertAll(
                () -> assertFalse(earlier.overlaps(later)),
                () -> assertFalse(later.overlaps(earlier))
        );
    }

    @Test
    void shouldCreatePeriodForRequestedNumberOfDays() {
        RentalPeriod period = RentalPeriod.forDays(START, 3);

        assertAll(
                () -> assertEquals(START, period.start()),
                () -> assertEquals(START.plusDays(3), period.end())
        );
    }

    @Test
    void shouldCreatePeriodForExactlyOneDay() {
        RentalPeriod period = RentalPeriod.forDays(START, 1);

        assertAll(
                () -> assertEquals(START, period.start()),
                () -> assertEquals(START.plusDays(1), period.end())
        );
    }

    @Test
    void shouldPreserveStartTimeWhenCalculatingEndDate() {
        LocalDateTime start =
                LocalDateTime.of(2026, 8, 10, 15, 45, 30);

        RentalPeriod period = RentalPeriod.forDays(start, 2);

        assertAll(
                () -> assertEquals(
                        LocalDateTime.of(2026, 8, 10, 15, 45, 30),
                        period.start()
                ),
                () -> assertEquals(
                        LocalDateTime.of(2026, 8, 12, 15, 45, 30),
                        period.end()
                )
        );
    }

    @Test
    void shouldCalculateEndDateAcrossMonthBoundary() {
        LocalDateTime start =
                LocalDateTime.of(2026, 8, 31, 10, 0);

        RentalPeriod period = RentalPeriod.forDays(start, 2);

        assertEquals(
                LocalDateTime.of(2026, 9, 2, 10, 0),
                period.end()
        );
    }

    @Test
    void shouldCalculateEndDateAcrossYearBoundary() {
        LocalDateTime start =
                LocalDateTime.of(2026, 12, 31, 10, 0);

        RentalPeriod period = RentalPeriod.forDays(start, 1);

        assertEquals(
                LocalDateTime.of(2027, 1, 1, 10, 0),
                period.end()
        );
    }

    @Test
    void shouldCalculateEndDateAcrossLeapDay() {
        LocalDateTime start =
                LocalDateTime.of(2028, 2, 28, 10, 0);

        RentalPeriod period = RentalPeriod.forDays(start, 2);

        assertEquals(
                LocalDateTime.of(2028, 3, 1, 10, 0),
                period.end()
        );
    }

    @Test
    void shouldRejectZeroRentalDays() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> RentalPeriod.forDays(START, 0)
                );

        assertNotNull(exception);
    }

    @Test
    void shouldRejectNegativeRentalDays() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> RentalPeriod.forDays(START, -1)
                );

        assertNotNull(exception);
    }

    @Test
    void shouldRejectVeryLargeNegativeRentalDays() {
        assertThrows(
                IllegalArgumentException.class,
                () -> RentalPeriod.forDays(
                        START,
                        Integer.MIN_VALUE
                )
        );
    }

    @Test
    void shouldRejectNullStartDateTime() {
        assertThrows(
                NullPointerException.class,
                () -> RentalPeriod.forDays(null, 2)
        );
    }

    @Test
    void shouldRejectNullPeriodWhenCheckingOverlap() {
        RentalPeriod period =
                RentalPeriod.forDays(START, 2);

        assertThrows(
                NullPointerException.class,
                () -> period.overlaps(null)
        );
    }

    @Test
    void shouldRejectPeriodWhenCalculatedEndExceedsSupportedDateRange() {
        LocalDateTime nearMaximumDate =
                LocalDateTime.MAX.minusDays(1);

        assertThrows(
                DateTimeException.class,
                () -> RentalPeriod.forDays(
                        nearMaximumDate,
                        2
                )
        );
    }
}
