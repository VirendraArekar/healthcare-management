import { api } from "./client";
export function resourcePath(kind: string, role: string) {
  return (
    (kind === "prescriptions" ? "/medical-records/prescriptions" : "/" + kind) +
    (role === "PATIENT" && ["lab", "billing"].includes(kind) ? "/my" : "")
  );
}
export async function listRecords(
  kind: string,
  role: string,
  page: number,
  q: string,
) {
  return (await api.get(resourcePath(kind, role), { params: { page, q } }))
    .data;
}
