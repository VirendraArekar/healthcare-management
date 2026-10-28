import { useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { useQuery } from "@tanstack/react-query";
import type { Field } from "../../types/form";
import { api } from "../../api/client";
function Lookup({ field, register }: { field: Field; register: any }) {
  const { data, isLoading, error } = useQuery({
    queryKey: ["lookup", field.lookup],
    queryFn: async () =>
      (await api.get(field.lookup!, { params: { size: 100 } })).data,
  });
  const rows = Array.isArray(data) ? data : (data?.content ?? []);
  return (
    <>
      <select
        {...register(field.key, {
          required: field.optional ? false : `${field.label} is required`,
        })}
      >
        <option value="">
          {isLoading
            ? "Loading…"
            : error
              ? "Unable to load options"
              : "Select " + field.label.toLowerCase()}
        </option>
        {rows.map((r: any) => (
          <option key={r.id} value={r[field.valueKey ?? "id"]}>
            {r.name ??
              r.email ??
              ([r.firstName, r.lastName].filter(Boolean).join(" ") ||
                r.medicine ||
                r.starts_at ||
                r.id)}
            {r.specialty ? " · " + r.specialty : ""}
            {r.starts_at ? " · " + new Date(r.starts_at).toLocaleString() : ""}
          </option>
        ))}
      </select>
      {error && (
        <small role="alert">
          Reference data could not be loaded. Refresh and try again.
        </small>
      )}
    </>
  );
}
export default function RecordForm({
  title,
  fields,
  busy,
  error,
  onClose,
  onSave,
  initialValues = {},
}: {
  title: string;
  fields: Field[];
  busy: boolean;
  error: string;
  onClose: () => void;
  onSave: (body: Record<string, string>) => void;
  initialValues?: Record<string, any>;
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Record<string, string>>({
    defaultValues: Object.fromEntries(
      Object.entries(initialValues).map(([k, v]) => [k, String(v ?? "")]),
    ),
  });
  const modal = useRef<HTMLElement>(null);
  useEffect(() => {
    const previous = document.activeElement as HTMLElement;
    modal.current?.querySelector<HTMLElement>("input,select,button")?.focus();
    const handler = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
      if (event.key === "Tab") {
        const nodes = modal.current?.querySelectorAll<HTMLElement>(
          "input,select,textarea,button:not(:disabled)",
        );
        if (!nodes?.length) return;
        const first = nodes[0],
          last = nodes[nodes.length - 1];
        if (event.shiftKey && document.activeElement === first) {
          event.preventDefault();
          last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
          event.preventDefault();
          first.focus();
        }
      }
    };
    document.addEventListener("keydown", handler);
    return () => {
      document.removeEventListener("keydown", handler);
      previous?.focus();
    };
  }, [onClose]);
  return (
    <div className="modal-backdrop">
      <section
        ref={modal}
        className="modal"
        role="dialog"
        aria-modal="true"
        aria-label={title}
      >
        <div className="panel-title">
          <h2>{title}</h2>
          <button onClick={onClose} aria-label="Close form">
            ×
          </button>
        </div>
        <form
          onSubmit={handleSubmit((body) => {
            for (const field of fields) {
              if (field.optional && body[field.key] === "")
                delete body[field.key];
              if (field.type === "datetime-local" && body[field.key])
                body[field.key] = new Date(body[field.key]).toISOString();
            }
            onSave(body);
          })}
        >
          {fields.map((f) => (
            <label key={f.key}>
              {f.label}
              {f.lookup ? (
                <Lookup field={f} register={register} />
              ) : f.options ? (
                <select {...register(f.key, { required: !f.optional })}>
                  <option value="">Select…</option>
                  {f.options.map((v) => (
                    <option key={v} value={v}>
                      {v}
                    </option>
                  ))}
                </select>
              ) : f.type === "textarea" ? (
                <textarea
                  rows={4}
                  {...register(f.key, {
                    required: !f.optional,
                    maxLength: 1999,
                  })}
                />
              ) : (
                <input
                  type={f.type ?? "text"}
                  step={f.type === "number" ? "0.01" : undefined}
                  min={f.min}
                  {...register(f.key, {
                    required: f.optional ? false : `${f.label} is required`,
                    min: f.min,
                    maxLength: 1999,
                  })}
                />
              )}{" "}
              {errors[f.key] && (
                <small role="alert" className="error">
                  {String(errors[f.key]?.message ?? "Check this value")}
                </small>
              )}
            </label>
          ))}
          {error && (
            <p role="alert" className="error">
              {error}
            </p>
          )}
          <div className="form-actions">
            <button type="button" onClick={onClose}>
              Cancel
            </button>
            <button className="primary" disabled={busy}>
              {busy ? "Saving…" : "Save record"}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}
