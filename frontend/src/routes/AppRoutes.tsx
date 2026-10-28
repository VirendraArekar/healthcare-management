import PrescriptionsPage from "../pages/prescriptions/PrescriptionsPage";
import AdministrationPage from "../pages/admin/AdministrationPage";
import { Routes, Route, Navigate } from "react-router-dom";
import DashboardPage from "../pages/dashboard/DashboardPage";
import ProtectedRoute from "./ProtectedRoute";
import { permissions } from "../config/navigation";
import PatientsPage from "../pages/patients/PatientsPage";
import StaffPage from "../pages/staff/StaffPage";
import AppointmentsPage from "../pages/appointments/AppointmentsPage";
import MedicalRecordsPage from "../pages/medical-records/MedicalRecordsPage";
import LaboratoryPage from "../pages/lab/LaboratoryPage";
import PharmacyPage from "../pages/pharmacy/PharmacyPage";
import BillingPage from "../pages/billing/BillingPage";
import NotificationsPage from "../pages/notifications/NotificationsPage";
import ReportsPage from "../pages/reports/ReportsPage";
export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<DashboardPage />} />
      <Route
        path="/patients"
        element={
          <ProtectedRoute roles={permissions["patients"]}>
            <PatientsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff"
        element={
          <ProtectedRoute roles={permissions["staff"]}>
            <StaffPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/appointments"
        element={
          <ProtectedRoute roles={permissions["appointments"]}>
            <AppointmentsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/medical-records"
        element={
          <ProtectedRoute roles={permissions["medical-records"]}>
            <MedicalRecordsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/lab"
        element={
          <ProtectedRoute roles={permissions["lab"]}>
            <LaboratoryPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/pharmacy"
        element={
          <ProtectedRoute roles={permissions["pharmacy"]}>
            <PharmacyPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/billing"
        element={
          <ProtectedRoute roles={permissions["billing"]}>
            <BillingPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/notifications"
        element={
          <ProtectedRoute roles={permissions["notifications"]}>
            <NotificationsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/reports"
        element={
          <ProtectedRoute roles={permissions["reports"]}>
            <ReportsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/administration"
        element={
          <ProtectedRoute roles={["ADMIN"]}>
            <AdministrationPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/prescriptions"
        element={
          <ProtectedRoute roles={permissions.prescriptions}>
            <PrescriptionsPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
