# Car Rental Technical Assessment

A small Java 21 implementation of the car-rental exercise. It deliberately uses no application framework, database, or HTTP layer so the code stays focused on object-oriented design, availability, and correctness.

## Requirements covered

- Reserve a `SEDAN`, `SUV`, or `VAN` for a start date/time and a positive number of days.
- Model a finite fleet, including multiple vehicles of the same type.
- Prevent the same physical car from being allocated to overlapping reservations.
- Allow back-to-back reservations, where one rental starts exactly when another ends.
- Prove the behaviour with unit tests.

## Run the tests

Prerequisites: JDK 21 and Maven 3.9+.

```bash
mvn clean test
```

## Design

- `Car` represents one physical vehicle. Inventory is therefore explicit instead of being a mutable counter.
- `RentalPeriod` owns date arithmetic and overlap rules. Periods are half-open: `[start, end)`. This permits a car returned at 10:00 to be rented again at 10:00.
- `Reservation` records which physical car was allocated and for which period.
- `CarRentalService` is the use-case boundary.
- `InMemoryCarRentalService` holds the fleet and reservations for this simulation. `reserve` is synchronized so availability checking and reservation creation are one atomic operation inside a service instance.

The implementation chooses the first available car of the requested type. This is deterministic and sufficient because the exercise defines no pricing, features, or allocation preference.

## Known limitations and trade-offs

- State is in memory and disappears when the process stops.
- `synchronized` protects only one JVM/service instance. A real distributed system would use database transactions, locking, or a uniqueness/exclusion constraint.
- There is no customer, cancellation, payment, pricing, time-zone, or vehicle-maintenance model because those behaviours are outside the brief.
- `LocalDateTime` follows the requirement's local date/time wording. Production software spanning time zones should normally accept a zone and persist an `Instant`.

## Production Considerations

This solution focuses on the core reservation logic and uses in-memory storage. For a production-ready system, the following areas should be addressed:

- **Persistent storage:** Store cars and reservations in a relational database.
- **Concurrency control:** Make the availability check and reservation creation atomic to prevent double booking.
- **Idempotency:** Use idempotency keys to prevent duplicate reservations when clients retry requests.
- **Reservation lifecycle:** Support statuses such as `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, and `EXPIRED`.
- **Validation:** Validate rental dates, duration, car type, vehicle status, and requests for past dates.
- **Time-zone handling:** Use `Instant` or `ZonedDateTime` when supporting multiple rental locations.
- **Vehicle availability:** Consider maintenance, damage, current rental status, and location when checking availability.
- **Security:** Add authentication, authorization, HTTPS, rate limiting, and secure handling of customer data.
- **Observability:** Add structured logging, correlation IDs, metrics, distributed tracing, health checks, and audit history.
- **API and error handling:** Expose versioned REST APIs with consistent, domain-specific error responses.
- **Scalability:** Use appropriate database indexes, pagination, horizontal scaling, and carefully controlled caching.
- **Testing:** Add integration, concurrency, API contract, performance, security, and end-to-end tests.

> The current implementation intentionally prioritizes domain modelling, rental-period overlap detection, inventory allocation, and unit-test coverage over infrastructure and production integration concerns.
