import { columnLabels } from "../../config/resourceColumns";
export function columnLabel(key: string) {
  return (
    columnLabels[key] ??
    key.replaceAll("_", " ").replace(/([a-z])([A-Z])/g, "$1 $2")
  );
}
export default function RecordCell({
  field,
  value,
  patients,
  staff,
}: {
  field: string;
  value: any;
  patients: Map<unknown, unknown>;
  staff: Map<unknown, unknown>;
}) {
  if (value === null || value === undefined || value === "") return <>—</>;
  if (field === "status")
    return (
      <span className={"pill status-" + String(value).toLowerCase()}>
        {String(value).replaceAll("_", " ")}
      </span>
    );
  if (field === "read")
    return (
      <span className={"pill " + (value ? "read" : "unread")}>
        {value ? "Read" : "Unread"}
      </span>
    );
  if (["patientId", "patient_id"].includes(field))
    return (
      <span title={String(value)}>
        {String(patients.get(String(value)) ?? String(value).slice(0, 8))}
      </span>
    );
  if (["doctorId", "doctor_id"].includes(field))
    return (
      <span title={String(value)}>
        {String(staff.get(String(value)) ?? String(value).slice(0, 8))}
      </span>
    );
  if (["total", "amount", "subtotal", "discount", "unitPrice"].includes(field))
    return (
      <span className="numeric">
        {Number(value).toLocaleString(undefined, {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        })}
      </span>
    );
  if (["dateOfBirth", "validUntil", "expires_on"].includes(field))
    return (
      <>
        {new Date(String(value) + "T00:00:00Z").toLocaleDateString(undefined, {
          year: "numeric",
          month: "short",
          day: "numeric",
          timeZone: "UTC",
        })}
      </>
    );
  if (["createdAt", "created_at", "starts_at", "startsAt"].includes(field))
    return (
      <>
        {new Date(value).toLocaleString(undefined, {
          month: "short",
          day: "numeric",
          hour: "2-digit",
          minute: "2-digit",
        })}
      </>
    );
  if (typeof value === "object")
    return <span title={JSON.stringify(value)}>View details</span>;
  return <>{String(value)}</>;
}
