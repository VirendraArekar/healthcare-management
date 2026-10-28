import { afterEach, it, expect } from "vitest";
import { render, screen, cleanup } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import {
  getAuthState,
  setAuthTokens,
  clearAuthTokens,
} from "../store/authStore";
import ProtectedRoute from "../routes/ProtectedRoute";
afterEach(() => {
  cleanup();
  clearAuthTokens();
});
it("clears account and tokens together on logout", () => {
  getAuthState().setUser({ email: "test@example.invalid", role: "DOCTOR" });
  setAuthTokens("test-access", "test-refresh");
  clearAuthTokens();
  expect(getAuthState().user).toBeNull();
  expect(getAuthState().accessToken).toBe("");
  expect(getAuthState().refreshToken).toBe("");
});
it("publishes immutable store snapshots", () => {
  const before = getAuthState();
  setAuthTokens("synthetic-access", "synthetic-refresh");
  expect(getAuthState()).not.toBe(before);
  expect(before.accessToken).toBe("");
});
it("blocks direct navigation to an administrator page", () => {
  getAuthState().setUser({ email: "patient@example.invalid", role: "PATIENT" });
  render(
    <MemoryRouter initialEntries={["/administration"]}>
      <Routes>
        <Route path="/" element={<p>Home workspace</p>} />
        <Route
          path="/administration"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <p>Account administration</p>
            </ProtectedRoute>
          }
        />
      </Routes>
    </MemoryRouter>,
  );
  expect(screen.queryByText("Account administration")).not.toBeInTheDocument();
  expect(screen.getByText("Home workspace")).toBeInTheDocument();
});
it("allows the configured role to access its page", () => {
  getAuthState().setUser({ email: "admin@example.invalid", role: "ADMIN" });
  render(
    <MemoryRouter>
      <ProtectedRoute roles={["ADMIN"]}>
        <p>Account administration</p>
      </ProtectedRoute>
    </MemoryRouter>,
  );
  expect(screen.getByText("Account administration")).toBeInTheDocument();
});
