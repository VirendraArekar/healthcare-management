import { useState } from "react";
import { api, errorMessage } from "../../api/client";
export default function RecoveryForm({ onBack }: { onBack: () => void }) {
  const [email, setEmail] = useState("");
  const [token, setToken] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  return (
    <section>
      <h2>Account recovery</h2>
      <form
        onSubmit={async (e) => {
          e.preventDefault();
          try {
            setError("");
            const { data } = await api.post("/auth/forgot-password", { email });
            setMessage(data.message);
          } catch (e) {
            setError(errorMessage(e));
          }
        }}
      >
        <label>
          Email
          <input
            required
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </label>
        <button>Send recovery instructions</button>
      </form>
      <form
        onSubmit={async (e) => {
          e.preventDefault();
          try {
            setError("");
            await api.post("/auth/reset-password", { token, password });
            setMessage("Password reset. Sign in with your new password.");
          } catch (e) {
            setError(errorMessage(e));
          }
        }}
      >
        <label>
          Single-use recovery token
          <input
            required
            value={token}
            onChange={(e) => setToken(e.target.value)}
          />
        </label>
        <label>
          New password
          <input
            type="password"
            minLength={12}
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        <button>Reset password</button>
      </form>
      {message && <p role="status">{message}</p>}
      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}
      <button onClick={onBack}>Back to sign in</button>
    </section>
  );
}
