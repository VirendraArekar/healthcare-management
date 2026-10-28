# Project structure and navigation

Java package names use the standard lowercase convention. A service class is `com.hospital.appointment.service.AppointmentService`; its controller is `com.hospital.appointment.controller.AppointmentController`.

```text
backend/
  platform-common/src/main/java/com/hospital/common/
    client/ config/ dao/ dto/ entity/ event/ mapper/ model/
    repository/ security/ service/ util/
  appointment-service/src/main/java/com/hospital/appointment/
    Application.java
    controller/AppointmentController.java
    service/AppointmentService.java
    repository/AppointmentRepository.java       # business persistence contract
    repository/jpa/AppointmentJpaRepository.java
    repository/jpa/AppointmentSlotJpaRepository.java
    dao/AppointmentDao.java                     # transactional SQL implementation
    entity/AppointmentEntity.java
    entity/AppointmentSlotEntity.java
    dto/BookingRequest.java
    dto/SlotRequest.java
    event/AppointmentEventPolicy.java
    event/StaffLeaveConsumer.java
    scheduler/ReminderScheduler.java
    config/KafkaRetry.java
  auth-service/ patient-service/ staff-service/
  medical-record-service/ lab-service/ pharmacy-service/
  billing-service/ notification-service/ reporting-service/
  api-gateway/                                # stateless routing and security
frontend/src/
  pages/                                      # route-level modules
    admin/ appointments/ auth/ billing/ dashboard/ lab/
    medical-records/ notifications/ patients/ pharmacy/
    prescriptions/ reports/ staff/
  components/                                 # shared UI and workflow features
    admin/ billing/ branding/ clinical/ data/ forms/ layout/ workflows/
  api/                                        # HTTP client and resource paths
  store/                                      # typed reactive authentication state
  routes/ hooks/ types/ config/ images/ test/
database/seed/                                 # deterministic synthetic data
infrastructure/                                # deployment and operations
postman/ scripts/ tests/ docs/
```

Each of the ten business services has controller, service, repository, DAO, entity, mapper, DTO and event packages. A package contains working code where that responsibility exists; the gateway deliberately has no fake repository/database. Service-specific events, consumers and scheduled work stay in their owning service.

## Request and event paths

HTTP → gateway → controller → service → repository interface → DAO → PostgreSQL. Controllers parse HTTP and enforce roles; services apply business rules and transaction boundaries. Spring Data JPA repositories map normalized aggregates. SQL DAOs handle explicit locks, guarded inventory/payment writes and reporting queries. Mappers convert persistence entities to API data; entities never become the public account/password API.

Patient profiles, staff profiles, departments, schedules, leave, encounters, vital signs, prescriptions, lab orders/tests, medicine catalogues, invoices/line items, refunds and notifications have dedicated typed entities and tables. Users, bookings, slots, medicine batches and reporting counts also have operational entity mappings. The common document record adapter remains for secondary compatible workflows such as appointment requests; it does not replace the normalized core model. Existing legacy records are backfilled by versioned migrations.

A transaction writes business state plus its outbox row. A publisher sends the envelope to Kafka. Consumers atomically claim the event ID and write a read model; retries route persistent failures to a dead-letter topic. Approved staff leave withdraws availability and cancels active bookings through an idempotent consumer.

## Practice an architecture change

1. Start with a controller DTO and a service rule. Keep HTTP annotations out of services and SQL out of both.
2. Add the repository operation before its DAO implementation; implement a Flyway migration for schema changes.
3. Update the typed entity/mapper when aggregate fields change. Preserve version checks.
4. Publish only workflow identifiers and allowed metadata; keep clinical notes/results out of Kafka.
5. Add meaningful rule, SQL invariant and gateway acceptance coverage.
6. Add the page under `src/pages`, reusable UI under `src/components`, HTTP access under `src/api`, and shared state under `src/store`.

Run `python3 scripts/check-architecture.py`. See `scripts/acceptance.py`, Java workflow tests and `tests/database/migrations.test.mjs` for examples.
