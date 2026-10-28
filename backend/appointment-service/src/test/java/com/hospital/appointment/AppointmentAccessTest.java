package com.hospital.appointment;

import static org.mockito.Mockito.*;

import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.appointment.service.AppointmentService;
import com.hospital.common.event.EventPublisher;
import com.hospital.common.service.AuditService;
import com.hospital.common.util.JsonCodec;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class AppointmentAccessTest {
  private final AppointmentRepository repository = mock(AppointmentRepository.class);
  private final AppointmentService service =
      new AppointmentService(
          repository,
          mock(EventPublisher.class),
          mock(AuditService.class),
          new JsonCodec(tools.jackson.databind.json.JsonMapper.builder().build()));

  @Test
  void doctorListUsesBoundStaffIdentity() {
    String staff = UUID.randomUUID().toString();
    var token =
        Jwt.withTokenValue("unit")
            .header("alg", "none")
            .subject(UUID.randomUUID().toString())
            .claim("roles", List.of("DOCTOR"))
            .claim("staffId", staff)
            .build();
    service.list(0, 20, token);
    verify(repository).findBookings("", "", staff, staff, 20, 0L);
  }

  @Test
  void unboundDoctorCannotQueryEveryAppointment() {
    var token =
        Jwt.withTokenValue("unit")
            .header("alg", "none")
            .subject(UUID.randomUUID().toString())
            .claim("roles", List.of("DOCTOR"))
            .build();
    service.list(0, 20, token);
    verify(repository).findBookings("", "", "unbound", "unbound", 20, 0L);
  }

  @Test
  void patientListUsesBoundPatientIdentity() {
    String patient = UUID.randomUUID().toString();
    var token =
        Jwt.withTokenValue("unit")
            .header("alg", "none")
            .subject(UUID.randomUUID().toString())
            .claim("roles", List.of("PATIENT"))
            .claim("patientId", patient)
            .build();
    service.list(0, 20, token);
    verify(repository).findBookings(patient, patient, "", "", 20, 0L);
  }
}
