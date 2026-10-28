import { beforeAll, afterAll, afterEach, it, expect } from "vitest";
import {
  render,
  screen,
  fireEvent,
  cleanup,
  within,
  waitFor,
} from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { http, HttpResponse } from "msw";
import { setupServer } from "msw/node";
import App from "../App";
import { clearTokens } from "../api/client";
let savedPatient: Record<string, string> | null = null;
const server = setupServer(
  http.get("/api/patients/references", () => HttpResponse.json([])),
  http.post("/api/auth/login", () =>
    HttpResponse.json({
      accessToken: "synthetic-test-access",
      refreshToken: "synthetic-test-refresh",
      role: "RECEPTIONIST",
    }),
  ),
  http.get("/api/:module", () =>
    HttpResponse.json({ content: [], totalElements: 0 }),
  ),
  http.post("/api/patients", async ({ request }) => {
    const body = (await request.json()) as Record<string, string>;
    savedPatient = body;
    return HttpResponse.json(
      { ...body, id: "synthetic-test-patient" },
      { status: 201 },
    );
  }),
);
beforeAll(() => server.listen({ onUnhandledRequest: "error" }));
afterAll(() => server.close());
afterEach(() => {
  savedPatient = null;
  server.resetHandlers();
  cleanup();
  clearTokens();
});
function app() {
  return render(
    <QueryClientProvider
      client={
        new QueryClient({ defaultOptions: { queries: { retry: false } } })
      }
    >
      <MemoryRouter>
        <App />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}
async function login() {
  fireEvent.change(screen.getByLabelText("Email address"), {
    target: { value: "receptionist@demo.hospital" },
  });
  fireEvent.change(screen.getByLabelText("Password"), {
    target: { value: "test-password" },
  });
  fireEvent.click(screen.getByRole("button", { name: /Sign in/ }));
  await within(
    await screen.findByRole("navigation", { name: "Main navigation" }),
  ).findByRole("link", { name: "Patients" });
}
it("shows receptionist navigation and persists a patient through the API", async () => {
  app();
  await login();
  expect(
    screen.queryByRole("link", { name: "Pharmacy" }),
  ).not.toBeInTheDocument();
  fireEvent.click(
    within(
      screen.getByRole("navigation", { name: "Main navigation" }),
    ).getByRole("link", { name: "Patients" }),
  );
  fireEvent.click(
    await screen.findByRole("button", { name: /Register patient/ }),
  );
  fireEvent.change(screen.getByLabelText("First name"), {
    target: { value: "Test" },
  });
  fireEvent.change(screen.getByLabelText("Last name"), {
    target: { value: "Synthetic" },
  });
  fireEvent.change(screen.getByLabelText("Date of birth"), {
    target: { value: "1990-01-01" },
  });
  fireEvent.change(screen.getByLabelText("Email"), {
    target: { value: "test@example.invalid" },
  });
  fireEvent.change(screen.getByLabelText("Phone"), {
    target: { value: "5550100" },
  });
  fireEvent.click(screen.getByRole("button", { name: "Save record" }));
  await waitFor(() =>
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument(),
  );
  expect(savedPatient).toMatchObject({
    firstName: "Test",
    lastName: "Synthetic",
    email: "test@example.invalid",
  });
});
it("displays backend login failures", async () => {
  server.use(
    http.post("/api/auth/login", () =>
      HttpResponse.json({ detail: "Invalid credentials" }, { status: 401 }),
    ),
  );
  app();
  fireEvent.change(screen.getByLabelText("Email address"), {
    target: { value: "bad@example.invalid" },
  });
  fireEvent.change(screen.getByLabelText("Password"), {
    target: { value: "wrong" },
  });
  fireEvent.click(screen.getByRole("button", { name: /Sign in/ }));
  expect(await screen.findByRole("alert")).toHaveTextContent(
    "Invalid credentials",
  );
});
