import { api } from "../../api/client";
import ResourcePage from "../../components/data/ResourcePage";
import WorkflowPanel from "../../components/workflows/WorkflowPanel";
export default function ReportsPage() {
  async function download(format: string) {
    const r = await api.get("/reports/" + format, { responseType: "blob" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(r.data);
    link.download = "hospital-report." + (format === "pdf" ? "pdf" : "csv");
    link.click();
    URL.revokeObjectURL(link.href);
  }
  return (
    <>
      <ResourcePage kind="reports" />
      <div className="heading-actions">
        <button onClick={() => download("export")}>Download CSV</button>
        <button onClick={() => download("pdf")}>Download PDF</button>
      </div>
      <WorkflowPanel
        title="Daily workloads and collected revenue"
        path="/reports/daily"
        roles={["ADMIN", "BILLING_STAFF"]}
      />
      <WorkflowPanel
        title="Monthly workloads and collected revenue"
        path="/reports/monthly"
        roles={["ADMIN", "BILLING_STAFF"]}
      />
      <WorkflowPanel
        title="Doctor utilization · next 30 days"
        path="/reports/doctor-utilization"
        roles={["ADMIN", "BILLING_STAFF"]}
      />
      <WorkflowPanel
        title="Current inventory by batch"
        path="/reports/stock"
        roles={["ADMIN"]}
      />
      <WorkflowPanel
        title="Stock and expiry alerts"
        path="/reports/expiry"
        roles={["ADMIN"]}
      />
      <WorkflowPanel
        title="Outstanding invoice balances"
        path="/reports/balances"
        roles={["ADMIN", "BILLING_STAFF"]}
      />
    </>
  );
}
