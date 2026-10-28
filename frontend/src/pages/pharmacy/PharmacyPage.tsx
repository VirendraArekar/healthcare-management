import ResourcePage from "../../components/data/ResourcePage";
import WorkflowPanel from "../../components/workflows/WorkflowPanel";
import { workflows } from "../../config/workflows";
export default function PharmacyPage() {
  return (
    <>
      <ResourcePage kind="pharmacy" />
      {workflows["pharmacy"].map((panel) => (
        <WorkflowPanel key={panel.path} {...panel} />
      ))}
    </>
  );
}
