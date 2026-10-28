import ResourcePage from "../../components/data/ResourcePage";
import WorkflowPanel from "../../components/workflows/WorkflowPanel";
import { workflows } from "../../config/workflows";
export default function NotificationsPage() {
  return (
    <>
      <ResourcePage kind="notifications" />
      {workflows["notifications"].map((panel) => (
        <WorkflowPanel key={panel.path} {...panel} />
      ))}
    </>
  );
}
