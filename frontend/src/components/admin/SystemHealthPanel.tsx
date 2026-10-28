import { useQuery } from "@tanstack/react-query";
import { FiServer, FiRefreshCw } from "react-icons/fi";
import { api, errorMessage } from "../../api/client";
export default function SystemHealthPanel() {
  const query = useQuery({
    queryKey: ["system-health"],
    queryFn: async () => (await api.get("/reports/health")).data,
    refetchInterval: 60000,
  });
  return (
    <section className="panel workflow-panel">
      <div className="panel-title">
        <div>
          <h2>System health</h2>
          <p className="muted">
            Live dependency checks across the service boundary.
          </p>
        </div>
        <button onClick={() => query.refetch()}>
          <FiRefreshCw />
          Refresh
        </button>
      </div>
      {query.error ? (
        <p role="alert" className="error">
          {errorMessage(query.error)}
        </p>
      ) : query.isLoading ? (
        <p>Checking services…</p>
      ) : (
        <div className="health-grid">
          {Object.entries(query.data ?? {}).map(([name, value]) => (
            <div className="health-card" key={name}>
              <FiServer />
              <span>{name}</span>
              <span className="pill">{String((value as any).status)}</span>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
