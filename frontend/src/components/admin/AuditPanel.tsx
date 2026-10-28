import { useState } from "react";
import WorkflowPanel from "../workflows/WorkflowPanel";
export default function AuditPanel() {
  const [service, setService] = useState("auth");
  return (
    <>
      <label className="audit-filter">
        Audit service{" "}
        <select value={service} onChange={(e) => setService(e.target.value)}>
          {[
            "auth",
            "patient",
            "staff",
            "appointment",
            "medical-record",
            "lab",
            "pharmacy",
            "billing",
            "notification",
            "reporting",
          ].map((name) => (
            <option key={name} value={name}>
              {name}
            </option>
          ))}
        </select>
      </label>
      <WorkflowPanel
        title="Security and operational audit"
        path={"/reports/audit?service=" + service}
        roles={["ADMIN"]}
      />
    </>
  );
}
