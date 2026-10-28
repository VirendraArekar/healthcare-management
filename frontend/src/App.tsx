import { useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useAuthStore } from "./store/authStore";
import LoginPage from "./pages/auth/LoginPage";
import AppShell from "./components/layout/AppShell";
export default function App() {
  const { user, setUser } = useAuthStore();
  const client = useQueryClient();
  useEffect(() => {
    const expired = () => {
      setUser(null);
      client.clear();
    };
    window.addEventListener("session-expired", expired);
    return () => window.removeEventListener("session-expired", expired);
  }, [client, setUser]);
  return user ? <AppShell /> : <LoginPage />;
}
