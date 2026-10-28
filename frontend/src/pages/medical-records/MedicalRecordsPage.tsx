import ResourcePage from "../../components/data/ResourcePage";
import ClinicalRecordPanel from "../../components/clinical/ClinicalRecordPanel";
export default function MedicalRecordsPage() {
  return (
    <>
      <ResourcePage kind="medical-records" />
      <ClinicalRecordPanel />
    </>
  );
}
