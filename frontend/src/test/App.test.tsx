import { describe, it, expect, afterEach } from "vitest";
import { render, screen, fireEvent, cleanup } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import App from "../App";
afterEach(cleanup);
function app() {
  return render(
    <QueryClientProvider client={new QueryClient()}>
      <MemoryRouter>
        <App />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}
describe("Authentication boundary", () => {
  it("requires login before exposing operations", () => {
    app();
    expect(screen.getByText("Sign in to your workspace")).toBeInTheDocument();
    expect(screen.queryByText("Register patient")).not.toBeInTheDocument();
  });
  it("validates empty login fields", async () => {
    app();
    fireEvent.click(screen.getByRole("button", { name: /Sign in/ }));
    expect(
      await screen.findByText("Enter a valid email address"),
    ).toBeInTheDocument();
    expect(await screen.findByText("Enter your password")).toBeInTheDocument();
  });
});
