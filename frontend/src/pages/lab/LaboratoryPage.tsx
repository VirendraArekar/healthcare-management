import ResourcePage from "../../components/data/ResourcePage";
import WorkflowPanel from "../../components/workflows/WorkflowPanel";
import { workflows } from "../../config/workflows";
export default function LaboratoryPage() {
  return (
    <>
      <ResourcePage kind="lab" />
      {workflows["lab"].map((panel) => (
        <WorkflowPanel key={panel.path} {...panel} />
      ))}
    </>
  );
}
