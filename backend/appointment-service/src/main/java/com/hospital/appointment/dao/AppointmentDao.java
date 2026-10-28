package com.hospital.appointment.dao;

import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.common.repository.RecordRepository;
import com.hospital.common.repository.jpa.ClinicalResourceJpaRepository;
import com.hospital.common.util.JsonCodec;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentDao extends RecordRepository implements AppointmentRepository {
  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.appointment.repository.jpa.AppointmentJpaRepository appointments;

  @org.springframework.beans.factory.annotation.Autowired
  private com.hospital.appointment.repository.jpa.AppointmentSlotJpaRepository slots;

  public AppointmentDao(JdbcTemplate db, JsonCodec codec, ClinicalResourceJpaRepository jpa) {
    super(db, codec, jpa);
  }

  public int insertSlot(
      java.util.UUID id, java.util.UUID doctor, java.sql.Timestamp start, java.sql.Timestamp end) {
    Object[] args = new Object[] {id, doctor, start, end};
    return db.update("insert into slots(id,doctor_id,starts_at,ends_at) values(?,?,?,?)", args);
  }

  public java.util.List<java.util.Map<String, Object>> findAvailableSlots() {
    Object[] args = new Object[] {};
    return db.queryForList(
        "select * from slots where enabled=true and starts_at>now() and id not in(select slot_id"
            + " from bookings where status in ('BOOKED','COMPLETED')) order by starts_at limit 100",
        args);
  }

  public java.util.List<java.util.Map<String, Object>> findBookings(
      String owner,
      String repeatedOwner,
      String doctor,
      String repeatedDoctor,
      int size,
      long offset) {
    Object[] args = new Object[] {owner, repeatedOwner, doctor, repeatedDoctor, size, offset};
    return db.queryForList(
        "select b.*,s.doctor_id,s.starts_at,s.ends_at from bookings b join slots s on"
            + " b.slot_id=s.id where (?='' or b.patient_id::text=?) and (?='' or"
            + " s.doctor_id::text=?) order by s.starts_at desc limit ? offset ?",
        args);
  }

  public java.util.List<java.util.Map<String, Object>> lockFutureSlot(java.util.UUID id) {
    Object[] args = new Object[] {id};
    return db.queryForList(
        "select * from slots where id=? and enabled=true and starts_at>now() for update", args);
  }

  public int insertBooking(java.util.UUID id, java.util.UUID slot, java.util.UUID patient) {
    Object[] args = new Object[] {id, slot, patient};
    return db.update(
        "insert into bookings(id,slot_id,patient_id,status) values(?,?,?,'BOOKED')", args);
  }

  public int cancelBooking(java.util.UUID id, String owner, String repeatedOwner) {
    Object[] args = new Object[] {id, owner, repeatedOwner};
    return db.update(
        "update bookings set status='CANCELLED',version=version+1 where id=? and status='BOOKED'"
            + " and (?='' or patient_id::text=?)",
        args);
  }

  public java.util.List<java.util.Map<String, Object>> lockRescheduleSlot(java.util.UUID id) {
    Object[] args = new Object[] {id};
    return db.queryForList(
        "select * from slots where id=? and enabled=true and starts_at>now() for update", args);
  }

  public int rescheduleBooking(java.util.UUID slot, java.util.UUID booking) {
    Object[] args = new Object[] {slot, booking};
    return db.update(
        "update bookings set slot_id=?,reminded_at=null,version=version+1 where id=? and"
            + " status='BOOKED'",
        args);
  }

  public java.util.List<java.util.Map<String, Object>> lockBooking(java.util.UUID id) {
    return db.queryForList(
        "select b.*,s.starts_at,s.doctor_id from bookings b join slots s on s.id=b.slot_id where"
            + " b.id=? for update of b",
        id);
  }

  public int completeBooking(java.util.UUID id, String status) {
    return db.update(
        "update bookings set status=?,version=version+1 where id=? and status='BOOKED'",
        status,
        id);
  }

  public java.util.List<java.util.Map<String, Object>> upcomingReminders() {
    return db.queryForList(
        "select b.* from bookings b join slots s on s.id=b.slot_id where b.status='BOOKED' and"
            + " s.starts_at between now() and now()+interval '24 hours' and b.reminded_at is null"
            + " for update of b skip locked");
  }

  public void markReminded(Object id) {
    db.update("update bookings set reminded_at=now() where id=?", id);
  }

  public java.util.List<java.util.Map<String, Object>> lockRequest(java.util.UUID id) {
    return db.queryForList(
        "select * from records where kind='appointment-request' and id=? for update", id);
  }

  public boolean claimLeaveEvent(java.util.UUID event) {
    return db.update(
            "insert into consumed_events(event_id) values(?) on conflict do nothing", event)
        == 1;
  }

  public java.util.List<java.util.Map<String, Object>> withdrawForLeave(
      java.util.UUID doctor, java.time.LocalDate start, java.time.LocalDate end) {
    var slots =
        db.queryForList(
            "select id from slots where doctor_id=? and starts_at>=? and starts_at<? order by id"
                + " for update",
            doctor,
            java.sql.Timestamp.from(start.atStartOfDay(java.time.ZoneOffset.UTC).toInstant()),
            java.sql.Timestamp.from(
                end.plusDays(1).atStartOfDay(java.time.ZoneOffset.UTC).toInstant()));
    var cancelled = new java.util.ArrayList<java.util.Map<String, Object>>();
    for (var slot : slots) {
      db.update("update slots set enabled=false,version=version+1 where id=?", slot.get("id"));
      cancelled.addAll(
          db.queryForList(
              "update bookings set"
                  + " status='CANCELLED',cancellation_reason='DOCTOR_LEAVE',version=version+1 where"
                  + " slot_id=? and status='BOOKED' returning id,patient_id",
              slot.get("id")));
    }
    return cancelled;
  }

  public java.util.Map<String, Object> detail(java.util.UUID id) {
    var appointment =
        appointments
            .findById(id)
            .orElseThrow(
                () ->
                    new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Appointment not found"));
    var slot = slots.findById(appointment.getSlotId()).orElseThrow();
    var response = new java.util.LinkedHashMap<String, Object>();
    response.put("id", appointment.getId());
    response.put("patientId", appointment.getPatientId().toString());
    response.put("slotId", slot.getId());
    response.put("doctorId", slot.getDoctorId().toString());
    response.put("startsAt", slot.getStartsAt());
    response.put("endsAt", slot.getEndsAt());
    response.put("status", appointment.getStatus());
    response.put("version", appointment.getVersion());
    response.put("cancellationReason", appointment.getCancellationReason());
    return response;
  }

  public java.util.List<java.util.Map<String, Object>> utilization(
      java.sql.Timestamp from, java.sql.Timestamp until) {
    return db.queryForList(
        "select doctor_id,count(*) as available_slots,count(*) filter(where occupied) as"
            + " occupied_slots,round(100.0*sum(case when occupied then minutes else 0"
            + " end)/nullif(sum(minutes),0),2) as utilization_percent from (select"
            + " s.doctor_id,extract(epoch from (s.ends_at-s.starts_at))/60 as minutes,exists(select"
            + " 1 from bookings b where b.slot_id=s.id and b.status in ('BOOKED','COMPLETED')) as"
            + " occupied from slots s where s.enabled=true and s.starts_at>=? and s.starts_at<?)"
            + " windows group by doctor_id order by doctor_id",
        from,
        until);
  }
}
