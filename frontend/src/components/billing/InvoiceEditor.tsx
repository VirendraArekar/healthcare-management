import { useState } from "react";
import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { api, errorMessage } from "../../api/client";
import { useAuthStore } from "../../store/authStore";
export default function InvoiceEditor() {
  const { user } = useAuthStore();
  const [patientId, setPatient] = useState("");
  const [tax, setTax] = useState("0");
  const [discount, setDiscount] = useState("0");
  const [lines, setLines] = useState([
    { description: "", quantity: 1, unitPrice: 0 },
  ]);
  const client = useQueryClient();
  const patients = useQuery({
    queryKey: ["billing-patient-references"],
    enabled: user?.role === "BILLING_STAFF",
    queryFn: async () => (await api.get("/patients/references")).data,
  });
  const mutation = useMutation({
    mutationFn: () =>
      api.post("/billing", {
        patientId,
        taxPercent: tax,
        discount,
        lineItems: lines,
      }),
    onSuccess: () => {
      client.invalidateQueries();
      setLines([{ description: "", quantity: 1, unitPrice: 0 }]);
    },
  });
  if (user?.role !== "BILLING_STAFF") return null;
  const subtotal = lines.reduce((v, l) => v + l.quantity * l.unitPrice, 0);
  return (
    <section className="panel workflow-panel">
      <h2>Itemized invoice</h2>
      <p className="muted">
        Calculated again on the server; payments are simulated.
      </p>
      <form
        onSubmit={(e) => {
          e.preventDefault();
          mutation.mutate();
        }}
      >
        <label>
          Patient
          <select
            required
            value={patientId}
            onChange={(e) => setPatient(e.target.value)}
          >
            <option value="">Select a patient</option>
            {patients.data?.content?.map((patient: any) => (
              <option key={patient.id} value={patient.id}>
                {patient.patientNumber} · {patient.name}
              </option>
            ))}
          </select>
        </label>
        {lines.map((line, i) => (
          <div className="invoice-line" key={i}>
            <label>
              Description
              <input
                required
                value={line.description}
                onChange={(e) =>
                  setLines((v) =>
                    v.map((l, j) =>
                      j === i ? { ...l, description: e.target.value } : l,
                    ),
                  )
                }
              />
            </label>
            <label>
              Quantity
              <input
                type="number"
                required
                min="1"
                value={line.quantity}
                onChange={(e) =>
                  setLines((v) =>
                    v.map((l, j) =>
                      j === i ? { ...l, quantity: Number(e.target.value) } : l,
                    ),
                  )
                }
              />
            </label>
            <label>
              Unit price
              <input
                type="number"
                required
                min="0"
                step="0.01"
                value={line.unitPrice}
                onChange={(e) =>
                  setLines((v) =>
                    v.map((l, j) =>
                      j === i ? { ...l, unitPrice: Number(e.target.value) } : l,
                    ),
                  )
                }
              />
            </label>
            <button
              type="button"
              disabled={lines.length === 1}
              onClick={() => setLines((v) => v.filter((_, j) => j !== i))}
            >
              Remove
            </button>
          </div>
        ))}
        <button
          type="button"
          onClick={() =>
            setLines((v) => [
              ...v,
              { description: "", quantity: 1, unitPrice: 0 },
            ])
          }
        >
          + Line item
        </button>
        <div className="invoice-line">
          <label>
            Tax %
            <input
              type="number"
              min="0"
              max="100"
              required
              value={tax}
              onChange={(e) => setTax(e.target.value)}
            />
          </label>
          <label>
            Discount
            <input
              type="number"
              min="0"
              max={subtotal}
              required
              value={discount}
              onChange={(e) => setDiscount(e.target.value)}
            />
          </label>
        </div>
        <p>
          Preview total:{" "}
          {((subtotal - Number(discount)) * (1 + Number(tax) / 100)).toFixed(2)}
        </p>
        {mutation.error && (
          <p role="alert" className="error">
            {errorMessage(mutation.error)}
          </p>
        )}
        {mutation.isSuccess && <p role="status">Invoice created.</p>}
        <button className="primary" disabled={mutation.isPending}>
          Create itemized invoice
        </button>
      </form>
    </section>
  );
}
