import { useQuery } from "@tanstack/react-query";
import { listRecords } from "../api/resources";
import { useAuthStore } from "../store/authStore";
export function useRecords(kind: string, page: number, q: string) {
  const { user } = useAuthStore();
  return useQuery({
    queryKey: [kind, page, q, user!.role],
    queryFn: () => listRecords(kind, user!.role, page, q),
  });
}
