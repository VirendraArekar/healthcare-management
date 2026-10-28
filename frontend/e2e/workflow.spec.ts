import { test, expect } from "@playwright/test";
test("receptionist registers a patient and books a persisted appointment", async ({
  page,
  request,
}) => {
  const password = process.env.DEMO_PASSWORD;
  if (!password) throw new Error("DEMO_PASSWORD required");
  const base = process.env.BASE_URL ?? "http://localhost:3000";
  const admin = await request.post(base + "/api/auth/login", {
    data: { email: "admin@demo.hospital", password },
  });
  expect(admin.ok()).toBeTruthy();
  const token = (await admin.json()).accessToken;
  const doctor = await request.post(base + "/api/staff", {
    headers: { Authorization: `Bearer ${token}` },
    data: {
      name: "Synthetic browser doctor",
      department: "General Medicine",
      specialty: "General",
    },
  });
  expect(doctor.ok()).toBeTruthy();
  const doctorId = (await doctor.json()).id;
  const starts = new Date(Date.now() + 7 * 86400000);
  starts.setUTCHours(10, 0, 0, 0);
  await request.post(base + "/api/staff/schedules", {
    headers: { Authorization: `Bearer ${token}` },
    data: {
      staffId: doctorId,
      dayOfWeek: [
        "SUNDAY",
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY",
      ][starts.getUTCDay()],
      startsAt: "09:00",
      endsAt: "17:00",
    },
  });
  const slot = await request.post(base + "/api/appointments/slots", {
    headers: { Authorization: `Bearer ${token}` },
    data: {
      doctorId,
      startsAt: starts.toISOString(),
      endsAt: new Date(starts.getTime() + 1800000).toISOString(),
    },
  });
  expect(slot.ok()).toBeTruthy();
  const slotId = (await slot.json()).id;
  await page.goto("/");
  await page.getByLabel("Email address").fill("receptionist@demo.hospital");
  await page.getByLabel("Password", { exact: true }).fill(password);
  await page.getByRole("button", { name: /Sign in/ }).click();
  await page
    .getByRole("navigation", { name: "Main navigation" })
    .getByRole("link", { name: "Patients", exact: true })
    .click();
  await page.getByRole("button", { name: /Register patient/ }).click();
  await page.getByLabel("First name").fill("Synthetic");
  await page.getByLabel("Last name").fill("E2E");
  await page.getByLabel("Date of birth").fill("1990-01-01");
  await page
    .getByLabel("Email", { exact: true })
    .fill("synthetic-e2e@example.invalid");
  await page.getByLabel("Phone").fill("5550100");
  const response = page.waitForResponse(
    (r) => r.url().endsWith("/api/patients") && r.request().method() === "POST",
  );
  await page.getByRole("button", { name: "Save record" }).click();
  const patient = await (await response).json();
  expect(patient.id).toBeTruthy();
  await page
    .getByRole("navigation", { name: "Main navigation" })
    .getByRole("link", { name: "Appointments", exact: true })
    .click();
  await page.getByRole("button", { name: /Book appointment/ }).click();
  await page.getByLabel("Patient", { exact: true }).selectOption(patient.id);
  await page.getByLabel("Available slot").selectOption(slotId);
  await page.getByRole("button", { name: "Save record" }).click();
  await expect(page.getByText("BOOKED", { exact: true }).first()).toBeVisible();
  await expect
    .poll(
      async () => {
        const r = await request.get(base + "/api/notifications", {
          headers: { Authorization: `Bearer ${token}` },
        });
        return JSON.stringify(await r.json());
      },
      { timeout: 60000 },
    )
    .toContain("appointment.booked.v1");
  const forbidden = await request.post(base + "/api/pharmacy", {
    headers: {
      Authorization: `Bearer ${(await (await request.post(base + "/api/auth/login", { data: { email: "receptionist@demo.hospital", password } })).json()).accessToken}`,
    },
    data: { medicine: "Forbidden", quantity: 1, expiresOn: "2030-01-01" },
  });
  expect(forbidden.status()).toBe(403);
});
