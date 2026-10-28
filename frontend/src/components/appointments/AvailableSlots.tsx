import { useQuery } from "@tanstack/react-query";
import { FiCalendar, FiClock } from "react-icons/fi";
import { useDirectoryLabels } from "../../hooks/useDirectoryLabels";
import { useAuthStore } from "../../store/authStore";
import { api, errorMessage } from "../../api/client";
export default function AvailableSlots() {
  const { user } = useAuthStore();
  const labels = useDirectoryLabels();
  const { data, error, isLoading } = useQuery({
    queryKey: ["slots"],
    queryFn: async () => (await api.get("/appointments/slots")).data,
    enabled: ["ADMIN", "RECEPTIONIST", "DOCTOR", "PATIENT"].includes(
      user!.role,
    ),
  });
  return (
    <details className="panel slots">
      <summary>
        <FiCalendar aria-hidden="true" /> Available appointment slots
      </summary>
      <p className="muted">
        Choose an available time in the booking or appointment request form.
      </p>
      {error ? (
        <p role="alert">{errorMessage(error)}</p>
      ) : isLoading ? (
        <p>Loading availability…</p>
      ) : !data?.length ? (
        <p className="muted">No published slots are available.</p>
      ) : (
        <div className="slot-grid">
          {data.map((slot: any) => (
            <article className="calendar-entry" key={slot.id}>
              <FiClock />
              <div>
                <strong>{new Date(slot.starts_at).toLocaleString()}</strong>
                <p>
                  {String(
                    labels.staff.get(String(slot.doctor_id)) ??
                      "Care team member",
                  )}
                </p>
                <small>
                  Until{" "}
                  {new Date(slot.ends_at).toLocaleTimeString([], {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </small>
              </div>
            </article>
          ))}
        </div>
      )}
    </details>
  );
}
