package com.hospital.patient.entity;

import com.hospital.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity(name = "Patient")
@Table(name = "patient_profiles")
public class PatientEntity extends BaseEntity {
  public PatientEntity() {}

  @Column(name = "patient_number", nullable = false, length = 64)
  private String patientNumber = null;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName = null;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName = null;

  @Column(name = "date_of_birth", nullable = false)
  private LocalDate dateOfBirth = null;

  @Column(name = "email", nullable = false, length = 254)
  private String email = null;

  @Column(name = "phone", nullable = true, length = 30)
  private String phone = null;

  @Column(name = "emergency_contact", nullable = true, columnDefinition = "text")
  private String emergencyContact = null;

  @Column(name = "allergies", nullable = true, columnDefinition = "text")
  private String allergies = null;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "ACTIVE";

  public String getPatientNumber() {
    return patientNumber;
  }

  public void setPatientNumber(String value) {
    this.patientNumber = value;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String value) {
    this.firstName = value;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String value) {
    this.lastName = value;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate value) {
    this.dateOfBirth = value;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String value) {
    this.phone = value;
  }

  public String getEmergencyContact() {
    return emergencyContact;
  }

  public void setEmergencyContact(String value) {
    this.emergencyContact = value;
  }

  public String getAllergies() {
    return allergies;
  }

  public void setAllergies(String value) {
    this.allergies = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String value) {
    this.status = value;
  }
}
