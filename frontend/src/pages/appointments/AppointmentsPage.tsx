import WorkflowPanel from "../../components/workflows/WorkflowPanel";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api, errorMessage } from "../../api/client";
import ResourcePage from "../../components/data/ResourcePage";
export default function AppointmentsPage() {
  const [day, setDay] = useState(new Date().toISOString().slice(0, 10));
  const { data, error } = useQuery({
    queryKey: ["daily-calendar", day],
    queryFn: async () =>
      (await api.get("/appointments", { params: { size: 100 } })).data,
  });
  const rows = (data?.content ?? []).filter(
    (r: any) => String(r.starts_at).slice(0, 10) === day,
  );
  return (
    <>
      <ResourcePage kind="appointments" />
      <section className="panel workflow-panel">
        <div className="panel-title">
          <h2>Daily appointment calendar</h2>
          <label>
            Date{" "}
            <input
              type="date"
              value={day}
              onChange={(e) => setDay(e.target.value)}
            />
          </label>
        </div>
        {error ? (
          <p role="alert">{errorMessage(error)}</p>
        ) : rows.length ? (
          rows.map((r: any) => (
            <article className="calendar-entry" key={r.id}>
              <strong>
                {new Date(r.starts_at).toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </strong>
              <span>Patient {r.patient_id}</span>
              <span className="pill">{r.status}</span>
            </article>
          ))
        ) : (
          <p className="muted">No appointments on this date.</p>
        )}
      </section>
      <WorkflowPanel
        title="Appointment requests"
        path="/appointments/requests"
        roles={["ADMIN", "RECEPTIONIST", "PATIENT"]}
        actions={[
          {
            title: "Request appointment",
            path: "/appointments/requests",
            roles: ["PATIENT"],
            fields: [
              {
                key: "slotId",
                label: "Available slot",
                lookup: "/appointments/slots",
              },
            ],
          },
          {
            title: "Approve request",
            path: "/appointments/requests/{id}/approve",
            row: true,
            roles: ["ADMIN", "RECEPTIONIST"],
            fields: [],
          },
        ]}
      />
    </>
  );
}
