import Brand from "../../components/branding/Brand";
import DeveloperFooter from "../../components/layout/DeveloperFooter";
import illustration from "../../images/care-illustration.svg";
import { FiArrowRight, FiShield, FiHeart, FiActivity } from "react-icons/fi";
import RecoveryForm from "./RecoveryForm";
import { useState } from "react";
import { useAuthStore } from "../../store/authStore";
import { api } from "../../api/client";
import { setTokens } from "../../api/client";
import { errorMessage } from "../../api/client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
const loginSchema = z.object({
  email: z.email("Enter a valid email address"),
  password: z.string().min(1, "Enter your password"),
});
export default function LoginPage() {
  const [recovering, setRecovering] = useState(false);
  const { setUser } = useAuthStore();
  const [error, setError] = useState("");
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<z.infer<typeof loginSchema>>({
    resolver: zodResolver(loginSchema),
  });
  return (
    <div className="login-page">
      <div className="login-story">
        <Brand />
        <p className="eyebrow">ONE CONNECTED CARE WORKSPACE</p>
        <h1>
          Better connected.
          <br />
          Better care.
        </h1>
        <p>
          Bring your teams, appointments and everyday hospital operations
          together.
        </p>
        <img
          className="care-illustration"
          src={illustration}
          alt="Connected patient records and hospital care"
        />
        <div className="login-benefits">
          <span>
            <FiShield />
            Secure by design
          </span>
          <span>
            <FiHeart />
            Patient-first workflows
          </span>
          <span>
            <FiActivity />
            One connected workspace
          </span>
        </div>
        <small>
          Synthetic demonstration platform • No real patient information
        </small>
      </div>
      <main className="login-form">
        {recovering ? (
          <RecoveryForm onBack={() => setRecovering(false)} />
        ) : (
          <>
            <p className="eyebrow">WELCOME BACK</p>
            <h2>Sign in to your workspace</h2>
            <p className="muted">Use your hospital account to continue.</p>
            <form
              onSubmit={handleSubmit(async (values) => {
                try {
                  setError("");
                  const { data } = await api.post("/auth/login", values);
                  setTokens(data.accessToken, data.refreshToken);
                  setUser({ email: values.email, role: data.role });
                } catch (e) {
                  setError(errorMessage(e));
                }
              })}
            >
              <label>
                Email address
                <input
                  autoComplete="username"
                  {...register("email")}
                  placeholder="you@hospital.com"
                />
              </label>
              {errors.email && <p role="alert">{errors.email.message}</p>}
              <label>
                Password
                <input
                  type="password"
                  autoComplete="current-password"
                  {...register("password")}
                />
              </label>
              {errors.password && <p role="alert">{errors.password.message}</p>}
              {error && (
                <div className="error" role="alert">
                  {error}
                </div>
              )}
              <button className="primary" disabled={isSubmitting}>
                {isSubmitting ? (
                  "Signing in…"
                ) : (
                  <>
                    Sign in <FiArrowRight aria-hidden="true" />
                  </>
                )}
              </button>
            </form>
            <button
              className="recovery-link"
              onClick={() => setRecovering(true)}
            >
              Forgot password?
            </button>
          </>
        )}
        <p className="footnote">
          Local demo: use a seeded role account and the password you configured
          in .env.
        </p>
      </main>
      <DeveloperFooter />
    </div>
  );
}
