import RecordCell, { columnLabel } from "../data/RecordCell";
import { useDirectoryLabels } from "../../hooks/useDirectoryLabels";
import { FiPlus } from "react-icons/fi";
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api, errorMessage } from "../../api/client";
import { useAuthStore } from "../../store/authStore";
import type { Field } from "../../types/form";
import RecordForm from "../forms/RecordForm";
export type WorkflowAction = {
  title: string;
  path: string;
  method?: "post" | "put" | "patch";
  roles: string[];
  fields: Field[];
  row?: boolean;
  initial?: Record<string, any>;
};
export default function WorkflowPanel({
  title,
  path,
  roles,
  actions = [],
}: {
  title: string;
  path: string;
  roles: string[];
  actions?: WorkflowAction[];
}) {
  const { user } = useAuthStore();
  const labels = useDirectoryLabels();
  const allowed = roles.includes(user!.role);
  const [selected, setSelected] = useState<{
    action: WorkflowAction;
    row?: any;
  } | null>(null);
  const [page, setPage] = useState(0);
  const client = useQueryClient();
  const query = useQuery({
    queryKey: ["workflow", path, page],
    enabled: allowed,
    queryFn: async () => (await api.get(path, { params: { page } })).data,
  });
  const mutation = useMutation({
    mutationFn: async (body: Record<string, string>) => {
      const a = selected!.action;
      const url = a.path.replace("{id}", String(selected?.row?.id ?? ""));
      return api.request({ url, method: a.method ?? "post", data: body });
    },
    onSuccess: () => {
      client.invalidateQueries();
      setSelected(null);
    },
  });
  if (!allowed) return null;
  const rows = Array.isArray(query.data)
    ? query.data
    : (query.data?.content ?? []);
  const keys = rows.length
    ? Object.keys(rows[0])
        .filter(
          (k) =>
            ![
              "id",
              "version",
              "password_hash",
              "passwordHash",
              "data",
            ].includes(k),
        )
        .slice(0, 8)
    : [];
  return (
    <section className="panel workflow-panel">
      <div className="panel-title">
        <div>
          <h2>{title}</h2>
          <p className="muted">
            {query.data?.totalElements !== undefined
              ? `${query.data.totalElements} records`
              : "Live workflow data"}
          </p>
        </div>
        <div className="heading-actions">
          {actions
            .filter((a) => !a.row && a.roles.includes(user!.role))
            .map((a) => (
              <button
                key={a.title}
                onClick={() => {
                  mutation.reset();
                  setSelected({ action: a });
                }}
              >
                <FiPlus aria-hidden="true" /> {a.title}
              </button>
            ))}
        </div>
      </div>
      {query.isLoading ? (
        <p>Loading…</p>
      ) : query.error ? (
        <p role="alert" className="error">
          {errorMessage(query.error)}
        </p>
      ) : !rows.length ? (
        <p className="muted">No records yet.</p>
      ) : (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                {keys.map((k) => (
                  <th key={k}>{columnLabel(k)}</th>
                ))}
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((row: any, i: number) => (
                <tr key={row.id ?? i}>
                  {keys.map((k) => (
                    <td key={k}>
                      <RecordCell
                        field={k}
                        value={row[k]}
                        patients={labels.patients}
                        staff={labels.staff}
                      />
                    </td>
                  ))}
                  <td>
                    {actions
                      .filter((a) => a.row && a.roles.includes(user!.role))
                      .map((a) => (
                        <button
                          key={a.title}
                          onClick={() => {
                            mutation.reset();
                            setSelected({ action: a, row });
                          }}
                        >
                          {a.title}
                        </button>
                      ))}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <div className="pagination">
        <span>Page {page + 1}</span>
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
      {selected && (
        <RecordForm
          title={selected.action.title}
          fields={selected.action.fields}
          busy={mutation.isPending}
          error={mutation.error ? errorMessage(mutation.error) : ""}
          initialValues={{ ...selected.row, ...selected.action.initial }}
          onClose={() => setSelected(null)}
          onSave={(body) => mutation.mutate(body)}
        />
      )}
    </section>
  );
}
