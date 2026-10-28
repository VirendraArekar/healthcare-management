import AuditPanel from "../../components/admin/AuditPanel";
import SystemHealthPanel from "../../components/admin/SystemHealthPanel";
import WorkflowPanel from "../../components/workflows/WorkflowPanel";
export default function AdministrationPage() {
  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Administration</h1>
          <p className="muted">Accounts, roles and the security audit trail.</p>
        </div>
      </div>
      <WorkflowPanel
        title="User accounts"
        path="/auth/users"
        roles={["ADMIN"]}
        actions={[
          {
            title: "Create account",
            path: "/auth/users",
            roles: ["ADMIN"],
            fields: [
              { key: "email", label: "Email", type: "email" },
              {
                key: "password",
                label: "Temporary password (12+ characters)",
                type: "password",
              },
              {
                key: "role",
                label: "Role",
                options: [
                  "ADMIN",
                  "DOCTOR",
                  "NURSE",
                  "RECEPTIONIST",
                  "PHARMACIST",
                  "LAB_TECHNICIAN",
                  "BILLING_STAFF",
                  "PATIENT",
                ],
              },
              {
                key: "staffId",
                label: "Staff profile",
                lookup: "/staff",
                optional: true,
              },
              {
                key: "patientId",
                label: "Patient profile",
                lookup: "/patients/references",
                optional: true,
              },
            ],
          },
          {
            title: "Change role or status",
            path: "/auth/users/{id}",
            method: "patch",
            row: true,
            roles: ["ADMIN"],
            fields: [
              {
                key: "role",
                label: "Role",
                options: [
                  "ADMIN",
                  "DOCTOR",
                  "NURSE",
                  "RECEPTIONIST",
                  "PHARMACIST",
                  "LAB_TECHNICIAN",
                  "BILLING_STAFF",
                  "PATIENT",
                ],
              },
              { key: "enabled", label: "Enabled", options: ["true", "false"] },
              {
                key: "staffId",
                label: "Staff profile",
                lookup: "/staff",
                optional: true,
              },
              {
                key: "patientId",
                label: "Patient profile",
                lookup: "/patients/references",
                optional: true,
              },
            ],
          },
        ]}
      />
      <SystemHealthPanel />
      <AuditPanel />
    </>
  );
}
