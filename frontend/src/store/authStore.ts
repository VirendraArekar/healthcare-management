import { useSyncExternalStore } from "react";
import type { User } from "../types/auth";
type AuthState = {
  user: User | null;
  accessToken: string;
  refreshToken: string;
  setUser: (user: User | null) => void;
};
const listeners = new Set<() => void>();
const setUser = (user: User | null) => {
  state = { ...state, user };
  listeners.forEach((fn) => fn());
};
let state: AuthState = {
  user: null,
  accessToken: "",
  refreshToken: "",
  setUser,
};
export const getAuthState = () => state;
export function setAuthTokens(accessToken: string, refreshToken: string) {
  state = { ...state, accessToken, refreshToken };
  listeners.forEach((fn) => fn());
}
export function clearAuthTokens() {
  state = { ...state, user: null, accessToken: "", refreshToken: "" };
  listeners.forEach((fn) => fn());
}
export function useAuthStore() {
  return useSyncExternalStore(
    (listener) => {
      listeners.add(listener);
      return () => {
        listeners.delete(listener);
      };
    },
    getAuthState,
    getAuthState,
  );
}
