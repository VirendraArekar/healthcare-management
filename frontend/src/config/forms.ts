import type { Field } from "../types/form";
export const forms: Record<
  string,
  { title: string; roles: string[]; fields: Field[] }
> = {
  patients: {
    title: "Register patient",
    roles: ["ADMIN", "RECEPTIONIST"],
    fields: [
      { key: "firstName", label: "First name" },
      { key: "lastName", label: "Last name" },
      { key: "dateOfBirth", label: "Date of birth", type: "date" },
      { key: "email", label: "Email", type: "email" },
      { key: "phone", label: "Phone" },
      { key: "emergencyContact", label: "Emergency contact", optional: true },
      { key: "allergies", label: "Allergies", optional: true },
    ],
  },
  staff: {
    title: "Add team member",
    roles: ["ADMIN"],
    fields: [
      { key: "name", label: "Full name" },
      {
        key: "department",
        label: "Department",
        lookup: "/departments",
        valueKey: "name",
      },
      { key: "specialty", label: "Specialty" },
    ],
  },
  appointments: {
    title: "Book appointment",
    roles: ["ADMIN", "RECEPTIONIST"],
    fields: [
      { key: "patientId", label: "Patient", lookup: "/patients" },
      {
        key: "slotId",
        label: "Available slot",
        lookup: "/appointments/slots",
      },
    ],
  },
  "medical-records": {
    title: "Create encounter",
    roles: ["DOCTOR"],
    fields: [
      { key: "patientId", label: "Patient", lookup: "/patients" },
      { key: "diagnosis", label: "Diagnosis" },
      { key: "notes", label: "Clinical notes" },
      { key: "treatment", label: "Treatment plan" },
    ],
  },
  lab: {
    title: "Order laboratory test",
    roles: ["ADMIN", "DOCTOR"],
    fields: [
      { key: "patientId", label: "Patient", lookup: "/patients" },
      {
        key: "test",
        label: "Test name",
        lookup: "/lab/catalog",
        valueKey: "name",
      },
    ],
  },
  pharmacy: {
    title: "Receive stock",
    roles: ["ADMIN", "PHARMACIST"],
    fields: [
      {
        key: "medicine",
        label: "Medicine",
        lookup: "/pharmacy/catalog",
        valueKey: "name",
      },
      { key: "quantity", label: "Quantity", type: "number" },
      { key: "expiresOn", label: "Expiry", type: "date" },
    ],
  },
  billing: {
    title: "Create invoice",
    roles: ["BILLING_STAFF"],
    fields: [
      {
        key: "patientId",
        label: "Patient",
        lookup: "/patients/references",
      },
      { key: "subtotal", label: "Subtotal", type: "number" },
      { key: "taxPercent", label: "Tax %", type: "number" },
      { key: "discount", label: "Discount", type: "number" },
    ],
  },
};
