import { useAuthStore } from "../../store/authStore";
import ResourcePage from "../../components/data/ResourcePage";
import type { WorkflowAction } from "../../components/workflows/WorkflowPanel";
export default function PatientsPage() {
  const { user } = useAuthStore();
  const actions: WorkflowAction[] =
    user?.role === "PATIENT"
      ? [
          {
            title: "Edit contact details",
            path: "/patients/me",
            method: "patch",
            roles: ["PATIENT"],
            fields: [
              { key: "email", label: "Email", type: "email" },
              { key: "phone", label: "Phone" },
              {
                key: "emergencyContact",
                label: "Emergency contact",
                optional: true,
              },
            ],
          },
        ]
      : [
          {
            title: "Edit",
            path: "/patients/{id}",
            method: "put",
            roles: ["ADMIN", "RECEPTIONIST"],
            fields: [
              { key: "firstName", label: "First name" },
              { key: "lastName", label: "Last name" },
              { key: "dateOfBirth", label: "Date of birth", type: "date" },
              { key: "email", label: "Email", type: "email" },
              { key: "phone", label: "Phone", optional: true },
              {
                key: "emergencyContact",
                label: "Emergency contact",
                optional: true,
              },
              { key: "allergies", label: "Allergies", optional: true },
              {
                key: "status",
                label: "Status",
                options: ["ACTIVE", "INACTIVE"],
              },
            ],
          },
          {
            title: "Assign care",
            path: "/patients/{id}/assignments",
            roles: ["ADMIN", "RECEPTIONIST"],
            fields: [
              {
                key: "clinicianId",
                label: "Clinician",
                lookup: "/auth/clinicians",
              },
              { key: "role", label: "Care role", options: ["DOCTOR", "NURSE"] },
            ],
          },
        ];
  return <ResourcePage kind="patients" extraActions={actions} />;
}
