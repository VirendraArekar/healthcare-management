import { useQuery } from "@tanstack/react-query";
import { useAuthStore } from "../store/authStore";
import { api } from "../api/client";
export function useDirectoryLabels() {
  const { user } = useAuthStore();
  const patients = useQuery({
    queryKey: ["directory-patient-labels", user?.role],
    queryFn: async () => (await api.get("/patients/references")).data,
    staleTime: 300000,
  });
  const staff = useQuery({
    queryKey: ["directory-staff-labels"],
    enabled:
      !!user &&
      ["ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST"].includes(user.role),
    queryFn: async () =>
      (await api.get("/staff", { params: { size: 100 } })).data,
    staleTime: 300000,
  });
  return {
    patients: new Map(
      (patients.data?.content ?? []).map((p: any) => [
        String(p.id),
        String(p.name),
      ]),
    ),
    staff: new Map(
      (staff.data?.content ?? []).map((p: any) => [
        String(p.id),
        String(p.name),
      ]),
    ),
  };
}
