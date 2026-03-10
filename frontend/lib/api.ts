import {
  ApiResponse,
  SignupRequest,
  LoginRequest,
  LoginResponse,
  SignupResponse,
  CreateApiKeyRequest,
  CreateApiKeyResponse,
  ApiKey,
  UpdateApiKeyDisabledRequest,
  Model,
  Provider,
  ModelProvider,
  OnRampResponse,
  User,
  UsageSummary,
  TimelineDataPoint,
  CostBreakdown,
  ApiKeyStats,
} from "@/types";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

class ApiError extends Error {
  constructor(
    public status: number,
    message: string
  ) {
    super(message);
    this.name = "ApiError";
  }
}

async function fetchApi<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token =
    typeof window !== "undefined" ? localStorage.getItem("token") : null;

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...((options.headers as Record<string, string>) || {}),
  };

  if (token && !endpoint.includes("/auth/")) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({
      success: false,
      message: "An error occurred",
    }));
    throw new ApiError(response.status, (error as { message?: string }).message || "Request failed");
  }

  const json = await response.json();
  // Backend wraps success responses in { success, message, data }
  if (json && typeof json.success === "boolean" && json.success && "data" in json) {
    return json.data as T;
  }
  return json as T;
}

// Auth API (backend: signup returns id+email, login returns token)
export const authApi = {
  signup: (data: SignupRequest) =>
    fetchApi<SignupResponse>("/v1/auth/signup", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  login: (data: LoginRequest) =>
    fetchApi<LoginResponse>("/v1/auth/login", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  logout: () => {
    if (typeof window !== "undefined") {
      localStorage.removeItem("token");
      localStorage.removeItem("userId");
    }
  },
};

// API Keys API
export const apiKeysApi = {
  create: (data: CreateApiKeyRequest) =>
    fetchApi<CreateApiKeyResponse>("/api/v1/api-keys/create", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  getAll: () => fetchApi<ApiKey[]>("/api/v1/api-keys"),

  updateDisabled: (id: string, data: UpdateApiKeyDisabledRequest) =>
    fetchApi<{}>(`/api/v1/api-keys/disable/${id}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    }),

  delete: (id: string) =>
    fetchApi<{}>(`/api/v1/api-keys/delete/${id}`, {
      method: "PATCH",
    }),
};

// Models API
export const modelsApi = {
  getAll: () => fetchApi<Model[]>("/api/v1/models"),

  getProviders: (modelId: string) =>
    fetchApi<ModelProvider[]>(`/api/v1/models/${modelId}/providers`),
};

// Providers API
export const providersApi = {
  getAll: () => fetchApi<Provider[]>("/api/v1/providers"),
};

// User API
export const userApi = {
  getProfile: () => fetchApi<User>("/api/v1/user/profile"),
};

// Payments API
export const paymentsApi = {
  onramp: () =>
    fetchApi<OnRampResponse>("/api/v1/payments/onramp", {
      method: "POST",
    }),
};

// Analytics API
export const analyticsApi = {
  getSummary: (params?: { period?: string; startDate?: string; endDate?: string }) => {
    const searchParams = new URLSearchParams();
    if (params?.period) searchParams.append("period", params.period);
    if (params?.startDate) searchParams.append("startDate", params.startDate);
    if (params?.endDate) searchParams.append("endDate", params.endDate);

    const query = searchParams.toString();
    return fetchApi<UsageSummary>(`/api/v1/analytics/summary${query ? `?${query}` : ""}`);
  },

  getTimeline: (params?: { period?: string; startDate?: string; endDate?: string }) => {
    const searchParams = new URLSearchParams();
    if (params?.period) searchParams.append("period", params.period);
    if (params?.startDate) searchParams.append("startDate", params.startDate);
    if (params?.endDate) searchParams.append("endDate", params.endDate);

    const query = searchParams.toString();
    return fetchApi<TimelineDataPoint[]>(`/api/v1/analytics/timeline${query ? `?${query}` : ""}`);
  },

  getBreakdown: (params?: { period?: string; startDate?: string; endDate?: string }) => {
    const searchParams = new URLSearchParams();
    if (params?.period) searchParams.append("period", params.period);
    if (params?.startDate) searchParams.append("startDate", params.startDate);
    if (params?.endDate) searchParams.append("endDate", params.endDate);

    const query = searchParams.toString();
    return fetchApi<CostBreakdown>(`/api/v1/analytics/breakdown${query ? `?${query}` : ""}`);
  },

  getApiKeyStats: (params?: { period?: string; startDate?: string; endDate?: string }) => {
    const searchParams = new URLSearchParams();
    if (params?.period) searchParams.append("period", params.period);
    if (params?.startDate) searchParams.append("startDate", params.startDate);
    if (params?.endDate) searchParams.append("endDate", params.endDate);

    const query = searchParams.toString();
    return fetchApi<ApiKeyStats[]>(`/api/v1/analytics/api-keys${query ? `?${query}` : ""}`);
  },
};

export { ApiError };
