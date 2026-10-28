import { resourceColumns } from "../../config/resourceColumns";
import RecordCell, { columnLabel } from "./RecordCell";
import { useDirectoryLabels } from "../../hooks/useDirectoryLabels";
import type { WorkflowAction } from "../workflows/WorkflowPanel";
import { FiSearch, FiRefreshCw, FiPlus } from "react-icons/fi";
import emptyState from "../../images/empty-state.svg";
import { useRecords } from "../../hooks/useRecords";
import { useState } from "react";
import { useEffect } from "react";

import { useQueryClient } from "@tanstack/react-query";
import { useMutation } from "@tanstack/react-query";
import { useAuthStore } from "../../store/authStore";
import { api } from "../../api/client";
import { errorMessage } from "../../api/client";
import { labels } from "../../config/navigation";

import { forms } from "../../config/forms";
import RecordForm from "../forms/RecordForm";
import AvailableSlots from "../appointments/AvailableSlots";
export default function ResourcePage({
  kind,
  extraActions = [],
}: {
  kind: string;
  extraActions?: WorkflowAction[];
}) {
  const labelsFor = useDirectoryLabels();
  const [selectedExtra, setSelectedExtra] = useState<{
    action: WorkflowAction;
    row: any;
  } | null>(null);
  const { user } = useAuthStore();
  const [page, setPage] = useState(0);
  const [q, setQ] = useState("");
  const [open, setOpen] = useState(false);
  const [error, setError] = useState("");
  const [actionRow, setActionRow] = useState<any>(null);
  const [action, setAction] = useState("");
  const client = useQueryClient();
  useEffect(() => {
    setPage(0);
    setQ("");
    setOpen(false);
    setError("");
    setActionRow(null);
  }, [kind]);
  const query = useRecords(kind, page, q);
  const rows = query.data?.content ?? [];
  const form = forms[kind];
  const mutate = useMutation({
    mutationFn: async ({
      path,
      body,
      headers,
      method,
    }: {
      path: string;
      body: any;
      headers?: any;
      method?: string;
    }) =>
      api.request({ url: path, method: method ?? "post", data: body, headers }),
    onSuccess: () => {
      client.invalidateQueries();
      setOpen(false);
      setSelectedExtra(null);
      setActionRow(null);
      setError("");
    },
    onError: (e) => setError(errorMessage(e)),
  });
  const columns =
    resourceColumns[kind] ??
    (rows.length
      ? Object.keys(rows[0])
          .filter((k) => !["id", "version"].includes(k))
          .slice(0, 7)
      : []);
  const privileged = (roles: string[]) => roles.includes(user!.role);
  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">CARE OPERATIONS</p>
          <h1>{labels[kind]}</h1>
          <p className="muted">
            Manage {labels[kind].toLowerCase()} with your team.
          </p>
        </div>
        <div className="heading-actions">
          {kind === "appointments" && privileged(["ADMIN", "DOCTOR"]) && (
            <button
              onClick={() => {
                setAction("slot");
                setActionRow({ id: "" });
              }}
            >
              Publish slot
            </button>
          )}
          {form?.roles.includes(user!.role) && (
            <button className="primary" onClick={() => setOpen(true)}>
              <FiPlus aria-hidden="true" /> {form.title}
            </button>
          )}
        </div>
      </div>
      <section className="panel">
        <div className="table-toolbar">
          <label className="search">
            <FiSearch aria-hidden="true" />
            <input
              aria-label="Search records"
              value={q}
              onChange={(e) => {
                setQ(e.target.value);
                setPage(0);
              }}
              placeholder="Search records…"
            />
          </label>
          <button onClick={() => query.refetch()}>
            <FiRefreshCw aria-hidden="true" />
            Refresh
          </button>
        </div>
        {error && (
          <div role="alert" className="error">
            {error}
          </div>
        )}
        {query.isLoading ? (
          <div className="empty">Loading records…</div>
        ) : query.error ? (
          <div role="alert" className="empty error">
            {errorMessage(query.error)}
          </div>
        ) : rows.length === 0 ? (
          <div className="empty">
            <img className="empty-state-image" src={emptyState} alt="" />
            <h2>No records yet</h2>
            <p className="muted">
              New {labels[kind].toLowerCase()} will appear here.
            </p>
          </div>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  {columns.map((c) => (
                    <th key={c}>{columnLabel(c)}</th>
                  ))}
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r: any, i: number) => (
                  <tr key={r.id ?? i}>
                    {columns.map((c) => (
                      <td key={c}>
                        <RecordCell
                          field={c}
                          value={r[c]}
                          patients={labelsFor.patients}
                          staff={labelsFor.staff}
                        />
                      </td>
                    ))}
                    <td>
                      <div className="row-actions">
                        {extraActions
                          .filter((a) => a.roles.includes(user!.role))
                          .map((a) => (
                            <button
                              key={a.title}
                              onClick={() => {
                                setError("");
                                setSelectedExtra({ action: a, row: r });
                              }}
                            >
                              {a.title}
                            </button>
                          ))}
                        {kind === "appointments" &&
                          user!.role === "DOCTOR" &&
                          r.status === "BOOKED" && (
                            <button
                              onClick={() =>
                                mutate.mutate({
                                  path: `/appointments/${r.id}/complete`,
                                  body: {},
                                })
                              }
                            >
                              Complete
                            </button>
                          )}
                        {kind === "notifications" && !r.read && (
                          <button
                            onClick={() =>
                              mutate.mutate({
                                path: `/notifications/${r.id}/read`,
                                body: {},
                              })
                            }
                          >
                            Mark read
                          </button>
                        )}
                        {kind === "billing" &&
                          user!.role === "BILLING_STAFF" &&
                          r.status === "PAID" && (
                            <button
                              onClick={() =>
                                mutate.mutate({
                                  path: `/billing/${r.id}/refund-request`,
                                  body: {},
                                })
                              }
                            >
                              Request refund
                            </button>
                          )}

                        {kind === "appointments" &&
                          r.status === "BOOKED" &&
                          privileged(["ADMIN", "RECEPTIONIST", "PATIENT"]) && (
                            <button
                              onClick={() =>
                                mutate.mutate({
                                  path: `/appointments/${r.id}/cancel`,
                                  body: {},
                                })
                              }
                            >
                              Cancel
                            </button>
                          )}
                        {kind === "appointments" &&
                          r.status === "BOOKED" &&
                          privileged(["ADMIN", "RECEPTIONIST"]) && (
                            <button
                              onClick={() => {
                                setAction("reschedule");
                                setActionRow(r);
                              }}
                            >
                              Reschedule
                            </button>
                          )}
                        {kind === "lab" &&
                          user!.role === "LAB_TECHNICIAN" &&
                          r.status !== "VERIFIED" && (
                            <button
                              onClick={() => {
                                setAction("lab");
                                setActionRow(r);
                              }}
                            >
                              Next step
                            </button>
                          )}
                        {kind === "pharmacy" && user!.role === "PHARMACIST" && (
                          <button
                            onClick={() => {
                              setAction("dispense");
                              setActionRow(r);
                            }}
                          >
                            Dispense
                          </button>
                        )}
                        {kind === "billing" && (
                          <button
                            onClick={async () => {
                              const response = await api.get(
                                `/billing/${r.id}/pdf`,
                                { responseType: "blob" },
                              );
                              const link = document.createElement("a");
                              link.href = URL.createObjectURL(response.data);
                              link.download = `invoice-${r.id}.pdf`;
                              link.click();
                              URL.revokeObjectURL(link.href);
                            }}
                          >
                            PDF
                          </button>
                        )}
                        {kind === "billing" &&
                          user!.role === "BILLING_STAFF" &&
                          r.status === "UNPAID" && (
                            <button
                              onClick={() =>
                                mutate.mutate({
                                  path: `/billing/${r.id}/pay`,
                                  body: {},
                                  headers: {
                                    "Idempotency-Key": crypto.randomUUID(),
                                  },
                                })
                              }
                            >
                              Simulate payment
                            </button>
                          )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <div className="pagination">
          <span>
            Page {page + 1}{" "}
            {query.data?.totalElements !== undefined &&
              `· ${query.data.totalElements} records`}
          </span>
          <div>
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
              Previous
            </button>
            <button
              disabled={rows.length < 20}
              onClick={() => setPage((p) => p + 1)}
            >
              Next
            </button>
          </div>
        </div>
      </section>
      {selectedExtra && (
        <RecordForm
          title={selectedExtra.action.title}
          fields={selectedExtra.action.fields}
          busy={mutate.isPending}
          error={error}
          initialValues={selectedExtra.row}
          onClose={() => setSelectedExtra(null)}
          onSave={(body) =>
            mutate.mutate({
              path: selectedExtra.action.path.replace(
                "{id}",
                String(selectedExtra.row.id),
              ),
              method: selectedExtra.action.method ?? "post",
              body,
            })
          }
        />
      )}
      {open && form && (
        <RecordForm
          title={form.title}
          fields={form.fields}
          busy={mutate.isPending}
          error={error}
          onClose={() => setOpen(false)}
          onSave={(body) => mutate.mutate({ path: "/" + kind, body })}
        />
      )}
      {actionRow && (
        <RecordForm
          title={
            {
              slot: "Publish doctor availability",
              reschedule: "Reschedule appointment",
              lab: "Update laboratory order",
              dispense: "Dispense prescription",
            }[action] ?? "Update"
          }
          fields={
            action === "slot"
              ? [
                  { key: "doctorId", label: "Doctor", lookup: "/staff" },
                  {
                    key: "startsAt",
                    label: "Starts at",
                    type: "datetime-local",
                  },
                  { key: "endsAt", label: "Ends at", type: "datetime-local" },
                ]
              : action === "reschedule"
                ? [{ key: "slotId", label: "New slot UUID" }]
                : action === "dispense"
                  ? [
                      { key: "quantity", label: "Quantity", type: "number" },
                      {
                        key: "prescriptionId",
                        label: "Prescription",
                        lookup: "/medical-records/prescriptions",
                      },
                    ]
                  : [
                      {
                        key: "status",
                        options: [
                          (
                            {
                              ORDERED: "COLLECTED",
                              COLLECTED: "PROCESSING",
                              PROCESSING: "RESULT_ENTERED",
                              RESULT_ENTERED: "VERIFIED",
                            } as Record<string, string>
                          )[actionRow.status],
                        ],
                        label: `Next status: ${{ ORDERED: "COLLECTED", COLLECTED: "PROCESSING", PROCESSING: "RESULT_ENTERED", RESULT_ENTERED: "VERIFIED" }[actionRow.status as string] ?? ""}`,
                      },
                      ...(actionRow.status === "PROCESSING"
                        ? [{ key: "result", label: "Test result" }]
                        : []),
                    ]
          }
          busy={mutate.isPending}
          error={error}
          onClose={() => setActionRow(null)}
          onSave={(body) =>
            mutate.mutate({
              path:
                action === "slot"
                  ? "/appointments/slots"
                  : action === "reschedule"
                    ? `/appointments/${actionRow.id}/reschedule`
                    : action === "dispense"
                      ? `/pharmacy/${actionRow.id}/dispense`
                      : `/lab/${actionRow.id}/transition`,
              body,
            })
          }
        />
      )}{" "}
      {kind === "appointments" && <AvailableSlots />}
    </>
  );
}
