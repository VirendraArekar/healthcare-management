import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  FiLogOut,
  FiChevronRight,
  FiBell,
  FiShield,
  FiMenu,
  FiX,
} from "react-icons/fi";
import { useState } from "react";
import { useAuthStore } from "../../store/authStore";
import { api, signOut } from "../../api/client";
import { permissions, labels } from "../../config/navigation";
import { navigationIcons } from "../../config/icons";
import Brand from "../branding/Brand";
import DeveloperFooter from "./DeveloperFooter";
import AppRoutes from "../../routes/AppRoutes";
export default function AppShell() {
  const { user, setUser } = useAuthStore();
  const client = useQueryClient();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const { data: notifications } = useQuery({
    queryKey: ["notification-badge"],
    queryFn: async () => (await api.get("/notifications")).data,
    refetchInterval: 30000,
  });
  const unread = (notifications?.content ?? []).filter(
    (n: any) => !n.read,
  ).length;
  const section = location.pathname.split("/")[1];
  const OverviewIcon = navigationIcons.overview;
  return (
    <div className={"shell " + (mobileOpen ? "mobile-nav-open" : "")}>
      <aside>
        <Brand />
        <span className="aside-label">CARE WORKSPACE</span>
        <nav aria-label="Main navigation">
          <NavLink to="/" end onClick={() => setMobileOpen(false)}>
            <OverviewIcon aria-hidden="true" />
            <span>Overview</span>
          </NavLink>
          {Object.keys(permissions)
            .filter((k) => permissions[k].includes(user!.role))
            .map((k) => {
              const Icon = navigationIcons[k] ?? navigationIcons.overview;
              return (
                <NavLink
                  key={k}
                  to={"/" + k}
                  onClick={() => setMobileOpen(false)}
                >
                  <Icon aria-hidden="true" />
                  <span>{labels[k]}</span>
                  {k === "notifications" && unread > 0 && (
                    <b className="nav-count">{unread}</b>
                  )}
                </NavLink>
              );
            })}
        </nav>
        <div className="sidebar-bottom">
          <div className="sidebar-security">
            <FiShield />
            <div>
              <strong>Secure care workspace</strong>
              <small>Role-based access enabled</small>
            </div>
          </div>
          <small>Healthcare management · v1.0</small>
        </div>
      </aside>
      {mobileOpen && (
        <button
          className="nav-overlay"
          aria-label="Close navigation"
          onClick={() => setMobileOpen(false)}
        />
      )}
      <div className="workspace">
        <header>
          <div className="breadcrumb">
            <button
              className="mobile-menu"
              aria-label={mobileOpen ? "Close navigation" : "Open navigation"}
              onClick={() => setMobileOpen((v) => !v)}
            >
              {mobileOpen ? <FiX /> : <FiMenu />}
            </button>
            <span>Workspace</span>
            <FiChevronRight />
            <strong>{labels[section] ?? "Overview"}</strong>
          </div>
          <div className="account">
            <NavLink
              to="/notifications"
              className="notification-button"
              aria-label={`Notifications${unread ? ", " + unread + " unread" : ""}`}
            >
              <FiBell />
              {unread > 0 && <i />}
            </NavLink>
            <div className="account-divider" />
            <div className="avatar">
              {user!.email.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <strong>{user!.email.split("@")[0].replaceAll("_", " ")}</strong>
              <small>{user!.role.replaceAll("_", " ")}</small>
            </div>
            <button
              className="signout-button"
              aria-label="Sign out"
              onClick={async () => {
                try {
                  await signOut();
                } catch {
                  /* Local session always clears. */
                }
                client.clear();
                setUser(null);
                navigate("/");
              }}
            >
              <FiLogOut />
              <span>Sign out</span>
            </button>
          </div>
        </header>
        <main>
          <AppRoutes />
        </main>
        <DeveloperFooter />
      </div>
    </div>
  );
}
