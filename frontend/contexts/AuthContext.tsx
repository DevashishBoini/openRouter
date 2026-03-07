"use client";

import React, { createContext, useContext, useState, useEffect } from "react";
import { authApi } from "@/lib/api";
import { LoginRequest, SignupRequest } from "@/types";

interface AuthContextType {
  isAuthenticated: boolean;
  userId: string | null;
  login: (data: LoginRequest) => Promise<void>;
  signup: (data: SignupRequest) => Promise<{ id: string; email: string }>;
  logout: () => void;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userId, setUserId] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const storedUserId = localStorage.getItem("userId");
    if (token) {
      setIsAuthenticated(true);
      setUserId(storedUserId);
    }
    setIsLoading(false);
  }, []);

  const login = async (data: LoginRequest) => {
    const response = await authApi.login(data);
    localStorage.setItem("token", response.token);
    setIsAuthenticated(true);
    setUserId(null);
  };

  const signup = async (data: SignupRequest) => {
    const response = await authApi.signup(data);
    return { id: response.id, email: response.email };
  };

  const logout = () => {
    authApi.logout();
    setIsAuthenticated(false);
    setUserId(null);
  };

  return (
    <AuthContext.Provider
      value={{ isAuthenticated, userId, login, signup, logout, isLoading }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
