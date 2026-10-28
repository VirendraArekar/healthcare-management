DROP INDEX one_booking_per_slot;
CREATE UNIQUE INDEX one_booking_per_slot ON bookings(slot_id) WHERE status IN ('BOOKED','COMPLETED');
