import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api, errorMessage } from "../../api/client";
import { useAuthStore } from "../../store/authStore";
import RecordForm from "../forms/RecordForm";
export default function ClinicalRecordPanel() {
  const { user } = useAuthStore();
  const [id, setId] = useState("");
  const [action, setAction] = useState<"edit" | "vitals" | "prescribe" | null>(
    null,
  );
  const client = useQueryClient();
  const records = useQuery({
    queryKey: ["clinical-record-picker"],
    queryFn: async () =>
      (await api.get("/medical-records", { params: { size: 100 } })).data,
  });
  const details = useQuery({
    queryKey: ["clinical-details", id],
    enabled: !!id,
    queryFn: async () => {
      const [record, history, vitals] = await Promise.all([
        api.get("/medical-records/" + id),
        api.get("/medical-records/" + id + "/history"),
        api.get("/medical-records/" + id + "/vitals"),
      ]);
      return {
        record: record.data,
        history: history.data.content,
        vitals: vitals.data.content,
      };
    },
  });
  const mutation = useMutation({
    mutationFn: (body: Record<string, string>) =>
      api.request({
        url:
          action === "edit"
            ? `/medical-records/${id}`
            : action === "vitals"
              ? `/medical-records/${id}/vitals`
              : "/medical-records/prescriptions",
        method: action === "edit" ? "put" : "post",
        data: body,
      }),
    onSuccess: () => {
      client.invalidateQueries();
      setAction(null);
    },
  });
  return (
    <section className="panel workflow-panel">
      <h2>Clinical record workspace</h2>
      <label>
        Encounter{" "}
        <select value={id} onChange={(e) => setId(e.target.value)}>
          <option value="">Choose an authorized encounter</option>
          {records.data?.content?.map((r: any) => (
            <option key={r.id} value={r.id}>
              {r.diagnosis} · {r.patientId} · {r.createdAt}
            </option>
          ))}
        </select>
      </label>
      {details.isLoading && id && <p>Loading clinical history…</p>}
      {details.error && (
        <p role="alert" className="error">
          {errorMessage(details.error)}
        </p>
      )}
      {details.data && (
        <>
          <div className="heading-actions">
            {user?.role === "DOCTOR" && (
              <>
                <button onClick={() => setAction("edit")}>
                  Edit clinical note
                </button>
                <button onClick={() => setAction("prescribe")}>
                  Write prescription
                </button>
              </>
            )}
            {["DOCTOR", "NURSE"].includes(user!.role) && (
              <button onClick={() => setAction("vitals")}>
                Record vital signs
              </button>
            )}
          </div>
          <dl className="clinical-summary">
            {["diagnosis", "notes", "treatment"].map((key) => (
              <div key={key}>
                <dt>{key}</dt>
                <dd>{details.data.record[key]}</dd>
              </div>
            ))}
          </dl>
          <h3>Observations & vital signs</h3>
          {details.data.vitals.length ? (
            details.data.vitals.map((r: any) => (
              <p key={r.id}>
                {r.createdAt} · {r.observation}
              </p>
            ))
          ) : (
            <p className="muted">No observations yet.</p>
          )}
          <h3>Record version history</h3>
          {details.data.history.map((r: any, i: number) => (
            <details key={i}>
              <summary>
                Version {r.version} · {r.created_at}
              </summary>
              <pre>{JSON.stringify(JSON.parse(r.data), null, 2)}</pre>
            </details>
          ))}
          {!details.data.history.length && (
            <p className="muted">No previous versions.</p>
          )}
        </>
      )}
      {action && details.data && (
        <RecordForm
          title={
            {
              edit: "Update encounter",
              vitals: "Record vital signs",
              prescribe: "Create prescription",
            }[action]
          }
          fields={
            action === "edit"
              ? [
                  { key: "diagnosis", label: "Diagnosis" },
                  { key: "notes", label: "Clinical notes", type: "textarea" },
                  {
                    key: "treatment",
                    label: "Treatment plan",
                    type: "textarea",
                  },
                ]
              : action === "vitals"
                ? [
                    {
                      key: "observation",
                      label: "Clinical observations",
                      type: "textarea",
                    },
                    ...[
                      "systolic",
                      "diastolic",
                      "pulse",
                      "temperature",
                      "oxygenSaturation",
                      "weight",
                    ].map((key) => ({
                      key,
                      label: (
                        {
                          systolic: "Systolic (mmHg)",
                          diastolic: "Diastolic (mmHg)",
                          pulse: "Pulse (beats/min)",
                          temperature: "Temperature (°C)",
                          oxygenSaturation: "Oxygen saturation (%)",
                          weight: "Weight (kg)",
                        } as Record<string, string>
                      )[key],
                      type: "number",
                      optional: true,
                    })),
                  ]
                : [
                    { key: "medicine", label: "Medicine" },
                    {
                      key: "quantity",
                      label: "Maximum quantity",
                      type: "number",
                      min: 1,
                    },
                    { key: "validUntil", label: "Valid until", type: "date" },
                    {
                      key: "instructions",
                      label: "Dosage instructions",
                      type: "textarea",
                    },
                  ]
          }
          initialValues={
            action === "edit"
              ? details.data.record
              : action === "prescribe"
                ? { patientId: details.data.record.patientId }
                : {}
          }
          busy={mutation.isPending}
          error={mutation.error ? errorMessage(mutation.error) : ""}
          onClose={() => setAction(null)}
          onSave={(body) => mutation.mutate(body)}
        />
      )}
    </section>
  );
}
