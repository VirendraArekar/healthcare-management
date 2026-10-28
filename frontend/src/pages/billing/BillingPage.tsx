import ResourcePage from "../../components/data/ResourcePage";
import WorkflowPanel from "../../components/workflows/WorkflowPanel";
import { workflows } from "../../config/workflows";
import InvoiceEditor from "../../components/billing/InvoiceEditor";
export default function BillingPage() {
  return (
    <>
      <ResourcePage kind="billing" />
      <InvoiceEditor />
      {workflows["billing"].map((panel) => (
        <WorkflowPanel key={panel.path} {...panel} />
      ))}
    </>
  );
}
