package com.hospital.appointment.repository;

import com.hospital.common.repository.ResourceOperations;

public interface AppointmentRepository extends ResourceOperations {
  int insertSlot(
      java.util.UUID id, java.util.UUID doctor, java.sql.Timestamp start, java.sql.Timestamp end);

  java.util.List<java.util.Map<String, Object>> findAvailableSlots();

  java.util.List<java.util.Map<String, Object>> findBookings(
      String owner,
      String repeatedOwner,
      String doctor,
      String repeatedDoctor,
      int size,
      long offset);

  java.util.List<java.util.Map<String, Object>> lockFutureSlot(java.util.UUID id);

  int insertBooking(java.util.UUID id, java.util.UUID slot, java.util.UUID patient);

  int cancelBooking(java.util.UUID id, String owner, String repeatedOwner);

  java.util.List<java.util.Map<String, Object>> lockRescheduleSlot(java.util.UUID id);

  int rescheduleBooking(java.util.UUID slot, java.util.UUID booking);

  java.util.List<java.util.Map<String, Object>> lockBooking(java.util.UUID id);

  int completeBooking(java.util.UUID id, String status);

  java.util.List<java.util.Map<String, Object>> upcomingReminders();

  void markReminded(Object id);

  java.util.List<java.util.Map<String, Object>> lockRequest(java.util.UUID id);

  java.util.List<java.util.Map<String, Object>> withdrawForLeave(
      java.util.UUID doctor, java.time.LocalDate start, java.time.LocalDate end);

  boolean claimLeaveEvent(java.util.UUID event);

  java.util.Map<String, Object> detail(java.util.UUID id);

  java.util.List<java.util.Map<String, Object>> utilization(
      java.sql.Timestamp from, java.sql.Timestamp until);
}
