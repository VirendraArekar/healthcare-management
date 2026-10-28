import axios from "axios";
import {
  getAuthState,
  setAuthTokens,
  clearAuthTokens,
} from "../store/authStore";
// Access tokens live only in memory. Refresh tokens are kept in the current tab's memory.
// A page reload intentionally requires a new login until secure cookie/BFF sessions are implemented.
export const api = axios.create({ baseURL: "/api", timeout: 15000 });
let refreshing: Promise<string> | null = null;
export const setTokens = setAuthTokens;
export const clearTokens = clearAuthTokens;
api.interceptors.request.use((config) => {
  if (getAuthState().accessToken)
    config.headers.Authorization = `Bearer ${getAuthState().accessToken}`;
  return config;
});
api.interceptors.response.use(
  (r) => r,
  async (error) => {
    const config = error.config;
    if (
      error.response?.status === 401 &&
      getAuthState().refreshToken &&
      !config._retry &&
      !config.url.includes("/auth/")
    ) {
      config._retry = true;
      if (!refreshing)
        refreshing = axios
          .post("/api/auth/refresh", {
            refreshToken: getAuthState().refreshToken,
          })
          .then((r) => {
            setTokens(r.data.accessToken, r.data.refreshToken);
            return getAuthState().accessToken;
          })
          .finally(() => {
            refreshing = null;
          });
      try {
        await refreshing;
        return api(config);
      } catch {
        clearTokens();
        window.dispatchEvent(new Event("session-expired"));
      }
    }
    return Promise.reject(error);
  },
);
export async function signOut() {
  try {
    await api.post("/auth/logout", {
      refreshToken: getAuthState().refreshToken,
    });
  } finally {
    clearTokens();
  }
}
export function errorMessage(e: unknown) {
  return axios.isAxiosError(e)
    ? (e.response?.data?.detail ??
        (e.response?.status === 403
          ? "Your role cannot perform this action."
          : e.message))
    : String(e);
}
