import ResourcePage from "../../components/data/ResourcePage";
import WorkflowPanel from "../../components/workflows/WorkflowPanel";
import { workflows } from "../../config/workflows";
export default function StaffPage() {
  return (
    <>
      <ResourcePage kind="staff" />
      {workflows["staff"].map((panel) => (
        <WorkflowPanel key={panel.path} {...panel} />
      ))}
    </>
  );
}
