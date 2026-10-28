import { resourcePath } from "../../api/resources";
import {
  FiArrowUpRight,
  FiCalendar,
  FiHeart,
  FiActivity,
  FiUsers,
  FiFileText,
} from "react-icons/fi";
import { NavLink } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useAuthStore } from "../../store/authStore";
import { api } from "../../api/client";
import { errorMessage } from "../../api/client";
import { permissions } from "../../config/navigation";
import { labels } from "../../config/navigation";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
export default function DashboardPage() {
  const { user } = useAuthStore();
  const allowed = Object.keys(permissions)
    .filter((k) => k !== "administration")
    .filter((k) => permissions[k].includes(user!.role));
  const { data, isLoading, error } = useQuery({
    queryKey: ["overview", user!.role],
    queryFn: async () =>
      Promise.all(
        allowed.map(async (k) => {
          const { data } = await api.get(resourcePath(k, user!.role));
          return {
            name: labels[k],
            count: data.totalElements ?? data.content?.length ?? 0,
          };
        }),
      ),
  });
  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">YOUR DAY, AT A GLANCE</p>
          <h1>
            {user!.role === "PATIENT"
              ? "Your health, connected."
              : "Welcome to your care workspace"}
          </h1>
          <p className="muted">
            A connected view of your hospital's daily operations.
          </p>
        </div>
        <span className="date-chip">
          {new Date().toLocaleDateString(undefined, {
            month: "long",
            day: "numeric",
            year: "numeric",
          })}
        </span>
      </div>
      {error && (
        <div className="error" role="alert">
          {errorMessage(error)}
        </div>
      )}
      <section className="welcome-banner">
        <div>
          <span className="welcome-tag">
            <FiHeart /> Every detail. Every department.
          </span>
          <h2>One workspace. Better connected care.</h2>
          <p>
            Keep appointments, care records and hospital operations moving
            together.
          </p>
        </div>
        <FiActivity className="welcome-pulse" aria-hidden="true" />
      </section>
      <div className="metrics">
        {isLoading ? (
          <p>Loading workspace…</p>
        ) : (
          data?.slice(0, 4).map((d, i) => (
            <article key={d.name}>
              <span className={"metric-icon color-" + i}>
                {
                  [
                    <FiUsers key="patients" />,
                    <FiCalendar key="appointments" />,
                    <FiFileText key="records" />,
                    <FiActivity key="activity" />,
                  ][i]
                }
              </span>
              <p>{d.name}</p>
              <strong>{d.count}</strong>
              <small>Current accessible records</small>
            </article>
          ))
        )}
      </div>
      <div className="overview-grid">
        <section className="panel">
          <div className="panel-title">
            <div>
              <h2>Workspace activity</h2>
              <p className="muted">Accessible records across your modules</p>
            </div>
            <span className="pill">Live data</span>
          </div>
          <div style={{ height: 280 }}>
            {data && (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={data}>
                  <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Bar dataKey="count" fill="#8b5cf6" radius={[5, 5, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </section>
        <section className="panel quick-actions">
          <p className="eyebrow">KEEP CARE MOVING</p>
          <h2>Your workspace</h2>
          <p className="muted">
            Open a module to view live records and continue a care workflow.
          </p>
          {allowed.slice(0, 5).map((k) => (
            <NavLink key={k} to={"/" + k}>
              {labels[k]} <FiArrowUpRight aria-hidden="true" />
            </NavLink>
          ))}
          <div className="notice">
            Synthetic data only. Access is checked by each backend service.
          </div>
        </section>
      </div>
    </>
  );
}
