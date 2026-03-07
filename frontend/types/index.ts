// API Response Types
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data: T;
}

// Auth Types
export interface SignupRequest {
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

// Login returns only token (backend LoginResponse)
export interface LoginResponse {
  token: string;
}

// Signup returns id and email (backend SignupResponse); no token
export interface SignupResponse {
  id: string;
  email: string;
}

export interface User {
  id: string;
  email: string;
  credits: number;
}

// API Key Types
export interface ApiKey {
  id: string;
  name: string;
  disabled: boolean;
  creditsConsumed: number;
  lastUsed: string | null;
  createdAt: string;
}

export interface CreateApiKeyRequest {
  apiKeyName: string;
}

// Backend ApiKeyCreateResponse returns apiKeyValue
export interface CreateApiKeyResponse {
  apiKeyValue: string;
}

export interface UpdateApiKeyDisabledRequest {
  disabled: boolean;
}

// Model & Provider Types
export interface Model {
  id: string;
  name: string;
  slug: string;
  companyId: string;
  companyName: string;
}

export interface Provider {
  id: string;
  name: string;
  website: string;
}

export interface ModelProvider {
  providerId: string;
  providerName: string;
  providerWebsite: string;
  inputTokenCost: number;
  outputTokenCost: number;
}

// Payment Types
export interface OnRampResponse {
  transactionId: string;
  amount: number;
  newBalance: number;
}
