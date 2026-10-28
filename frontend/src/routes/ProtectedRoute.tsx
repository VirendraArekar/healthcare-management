import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
export default function ProtectedRoute({
  roles,
  children,
}: {
  roles: string[];
  children: ReactNode;
}) {
  const { user } = useAuthStore();
  return user && roles.includes(user.role) ? (
    children
  ) : (
    <Navigate to="/" replace />
  );
}
