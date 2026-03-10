# OpenRouter - Multi-LLM API Gateway Platform

A comprehensive platform for routing and managing requests across multiple Large Language Model (LLM) providers with built-in authentication, usage tracking, and analytics. Features a model-agnostic call format for seamless integration across different LLM providers.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Database Setup](#2-database-setup)
  - [3. Backend Configuration](#3-backend-configuration)
  - [4. Gateway Configuration](#4-gateway-configuration)
  - [5. Frontend Configuration](#5-frontend-configuration)
- [Running the Applications](#running-the-applications)
- [Features](#features)
  - [Backend (Portal Service)](#backend-portal-service)
  - [Gateway (LLM API Router)](#gateway-llm-api-router)
  - [Frontend Dashboard](#frontend-dashboard)
- [License](#license)

## Overview

OpenRouter is a three-tier application consisting of:
- **Backend**: User management, authentication, and portal services
- **Gateway**: LLM API routing and request handling
- **Frontend**: Web-based dashboard for managing API keys and monitoring usage

The platform supports multiple LLM providers (OpenAI, Anthropic, Google) and provides a unified API interface for accessing various models.

## Architecture

```
┌─────────────┐
│  Frontend   │  (Next.js - Port 3000)
│  Dashboard  │
└──────┬──────┘
       │
       ├─────────────┐
       │             │
┌──────▼──────┐ ┌───▼────────┐
│   Backend   │ │  Gateway   │
│  (Portal)   │ │ (LLM API)  │
│  Port 8080  │ │ Port 8081  │
└──────┬──────┘ └─────┬──────┘
       │              │
       └──────┬───────┘
              │
       ┌──────▼──────┐
       │  PostgreSQL │
       │  Database   │
       └─────────────┘
```

## Tech Stack

### Backend (Portal Service)
- **Framework**: Spring Boot 3.2.3
- **Language**: Java 17+
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven
- **Key Libraries**:
  - JJWT (JWT handling)
  - Lombok (code generation)
  - Bean Validation
  - BCrypt (password hashing)

### Gateway (LLM API Router)
- **Framework**: Spring Boot 3.2.3
- **Language**: Java 17+
- **Database**: PostgreSQL (shared with backend)
- **Security**: API Key authentication
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven
- **Key Features**:
  - Multi-provider routing (OpenAI, Anthropic, Google)
  - Rate limiting
  - Usage tracking and cost calculation
  - Model pricing with configurable commission

### Frontend
- **Framework**: Next.js 16.1.6
- **Language**: TypeScript 5.9.3
- **Styling**: Tailwind CSS 4.2.1
- **UI Components**: Radix UI, Shadcn
- **Key Libraries**:
  - React 19.2.4
  - Lucide React (icons)
  - Sonner (toast notifications)

## Prerequisites

Before setting up the project, ensure you have:

- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: Version 3.6+
- **Node.js**: Version 18+ (for frontend)
- **npm** or **yarn**: Latest version
- **PostgreSQL**: Version 13+ (local or cloud instance like Supabase)
- **Git**: For cloning the repository

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd openRouter
```

### 2. Database Setup

Create a PostgreSQL database (or use a hosted service like Supabase):

```sql
CREATE DATABASE openrouter;
```

The applications will automatically create tables on startup using Hibernate's `ddl-auto=update` configuration.

### 3. Backend Configuration

#### Create Configuration File

Navigate to the backend directory and create a local configuration file:

```bash
cd backend/src/main/resources
cp application.properties application-local.properties
```

#### Configure `application-local.properties`

Update the following properties with your actual values:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://<YOUR-SUPABASE-HOST>:5432/postgres
spring.datasource.username=<YOUR-USERNAME>
spring.datasource.password=<YOUR-DATABASE-PASSWORD>
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=<YOUR-JWT-SECRET-KEY-MINIMUM-256-BITS>
jwt.expiration=3600000  # 1 hour in milliseconds

# Password & API Key Hashing
passwordHandler.salt_rounds=12
apiKeyHandler.salt_rounds=12
```

**Important**: Generate a secure JWT secret (at least 256 bits):
```bash
openssl rand -base64 32
```

### 4. Gateway Configuration

#### Create Configuration File

Navigate to the gateway directory and create a local configuration file:

```bash
cd gateway/src/main/resources
cp application.properties application-local.properties
```

#### Configure `application-local.properties`

Update the following properties:

```properties
# Server Configuration
server.port=8081

# Database Configuration (same as backend)
spring.datasource.url=jdbc:postgresql://<YOUR-SUPABASE-HOST>:5432/postgres
spring.datasource.username=<YOUR-USERNAME>
spring.datasource.password=<YOUR-DATABASE-PASSWORD>

# OpenAI Configuration
provider.openai.api-key=<YOUR-OPENAI-API-KEY>
provider.openai.base-url=https://api.openai.com/v1
provider.openai.timeout.connect=5000
provider.openai.timeout.read=120000

# Anthropic Configuration
provider.anthropic.api-key=<YOUR-ANTHROPIC-API-KEY>
provider.anthropic.base-url=https://api.anthropic.com/v1
provider.anthropic.version=2023-06-01
provider.anthropic.timeout.connect=5000
provider.anthropic.timeout.read=120000

# Google (Gemini) Configuration
provider.google.api-key=<YOUR-GOOGLE-API-KEY>
provider.google.base-url=https://generativelanguage.googleapis.com/v1beta
provider.google.timeout.connect=5000
provider.google.timeout.read=120000

# Pricing Configuration
gateway.pricing.commission-multiplier=1.05  # 5% markup on provider cost

# Rate Limiting
gateway.ratelimit.requests-per-hour=1000
gateway.ratelimit.enabled=true
```

### 5. Frontend Configuration

Create a `.env.local` file in the frontend directory:

```bash
cd frontend
touch .env.local
```

Add the following environment variables:

```env
NEXT_PUBLIC_BACKEND_URL=http://localhost:8080
NEXT_PUBLIC_GATEWAY_URL=http://localhost:8081
```

## Running the Applications

### Start Backend Service

```bash
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The backend will start on `http://localhost:8080`

### Start Gateway Service

```bash
cd gateway
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The gateway will start on `http://localhost:8081`

### Start Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:3000`

## Features

### Backend (Portal Service)

The backend service handles user management, authentication, and provides a comprehensive portal for managing API access and monitoring usage.

**Core Features**:
- JWT-based authentication and authorization
- Secure password hashing with BCrypt
- API key generation and management with secure hashing
- Real-time usage analytics and cost tracking
- Credit-based billing system
- Global exception handling and standardized API responses
- Request tracing with correlation IDs
- Comprehensive logging and monitoring

#### Authentication & User Management
- **POST** `/v1/auth/signup` - User registration
  - Email validation and uniqueness check
  - Secure password hashing (BCrypt with 12 rounds)
  - Automatic user account creation
- **POST** `/v1/auth/login` - User login with JWT token generation
  - Credential validation
  - JWT token generation with configurable expiration
  - Returns user profile and authentication token
- **GET** `/api/v1/users/profile` - Get user profile
  - Requires JWT authentication
  - Returns user details and account information

#### API Key Management
- **POST** `/api/v1/api-keys` - Create new API key
  - Generates unique, cryptographically secure API keys
  - Supports custom naming and descriptions
  - Returns plain-text key only once (hashed in database)
- **GET** `/api/v1/api-keys` - List all user API keys
  - Returns metadata without exposing actual keys
  - Shows creation date, usage stats, and status
- **GET** `/api/v1/api-keys/{id}` - Get specific API key details
  - Detailed information including last used timestamp
- **PATCH** `/api/v1/api-keys/{id}/disabled-status` - Enable/disable API key
  - Soft disable without deletion
  - Immediately prevents further API usage
- **DELETE** `/api/v1/api-keys/{id}` - Delete API key
  - Permanent deletion from database
  - Cannot be recovered

#### Analytics & Monitoring
- **GET** `/api/v1/analytics/summary` - Usage summary
  - Total requests, costs, tokens (input/output)
  - Success rate and error statistics
  - Support for preset periods (today, 7days, 30days, all)
  - Custom date range filtering
- **GET** `/api/v1/analytics/cost-breakdown` - Cost breakdown
  - Grouped by model and provider
  - Shows per-model usage statistics
  - Cost attribution analysis
- **GET** `/api/v1/analytics/timeline` - Usage timeline data
  - Time-series data for charts
  - Hourly/daily granularity
  - Request volume and cost trends
- **GET** `/api/v1/analytics/recent-requests` - Recent API requests
  - Paginated request history
  - Includes model, cost, tokens, status
  - Useful for debugging and auditing

#### Model Management
- **GET** `/api/v1/models` - List all available models
  - All supported models across providers
  - Pricing information per model
  - Model capabilities and context windows
- **GET** `/api/v1/models/{id}` - Get model details
  - Detailed model specifications
  - Provider information
  - Current pricing

#### Payment Management
- **POST** `/api/v1/payments/add-credits` - Add credits to user account
  - Credit-based prepaid system
  - Transaction history tracking
- **GET** `/api/v1/payments/balance` - Get current credit balance
  - Real-time balance information
  - Includes pending transactions

### Gateway (LLM API Router)

The gateway service acts as an intelligent router for LLM API requests, providing a unified OpenAI-compatible interface for multiple providers with built-in cost tracking, rate limiting, and usage analytics.

**Core Features**:
- Multi-provider support (OpenAI, Anthropic, Google Gemini)
- OpenAI-compatible API interface
- API key authentication with secure hashing
- Automatic provider routing based on model slugs
- Real-time cost calculation with configurable commission
- Rate limiting per API key (requests per hour)
- Usage tracking and analytics integration
- Request/response transformation for provider-specific formats
- Token counting and cost attribution
- Comprehensive error handling and provider fallback support

#### Chat Completions
- **POST** `/v1/chat/completions` - OpenAI-compatible chat completions endpoint
  - Accepts standard OpenAI format requests
  - Automatically routes to the appropriate provider
  - Transforms requests/responses per provider requirements
  - Tracks tokens, costs, and usage in real-time

**Request Format**:
```json
{
  "model": "openai/gpt-4",
  "messages": [
    {"role": "user", "content": "Hello!"}
  ],
  "temperature": 0.7,
  "max_tokens": 150
}
```

**Response Format** (OpenAI-compatible):
```json
{
  "id": "chatcmpl-xyz",
  "object": "chat.completion",
  "created": 1234567890,
  "model": "openai/gpt-4",
  "choices": [{
    "index": 0,
    "message": {
      "role": "assistant",
      "content": "Hello! How can I help you?"
    },
    "finish_reason": "stop"
  }],
  "usage": {
    "prompt_tokens": 10,
    "completion_tokens": 12,
    "total_tokens": 22
  }
}
```

**Supported Model Slug Formats**:
- `openai/gpt-4` - OpenAI GPT-4
- `openai/gpt-3.5-turbo` - OpenAI GPT-3.5
- `anthropic/claude-3-opus` - Anthropic Claude 3 Opus
- `anthropic/claude-3-sonnet` - Anthropic Claude 3 Sonnet
- `anthropic/claude-3-haiku` - Anthropic Claude 3 Haiku
- `google/gemini-pro` - Google Gemini Pro
- `google/gemini-1.5-flash` - Google Gemini 1.5 Flash

**Provider-Specific Features**:
- **OpenAI**: Native support, direct API passthrough
- **Anthropic**: Message format transformation, version header injection
- **Google**: Content format conversion, safety settings handling

**Rate Limiting**:
- Configurable requests per hour per API key
- Automatic request counting and throttling
- Returns `429 Too Many Requests` when limit exceeded
- Rate limit tracking stored in database
- Can be enabled/disabled globally

**Cost Calculation**:
- Per-token pricing based on model and provider
- Input and output tokens tracked separately
- Configurable commission multiplier (e.g., 1.05 = 5% markup)
- Real-time cost calculation and credit deduction
- Detailed cost breakdown per request

**Authentication**:
- API key-based authentication via `Authorization: Bearer <key>` header
- Secure API key validation with BCrypt
- Automatic user and API key association
- Disabled key rejection

**Usage Tracking**:
- Every request logged with metadata
- Tracks: model, provider, tokens, cost, status, timestamp
- Integrates with backend analytics service
- Failed request tracking for debugging
- Request correlation IDs for tracing

### Frontend Dashboard

#### Pages & Features
- **Landing Page** (`/`) - Platform overview
- **Login** (`/login`) - User authentication
- **Signup** (`/signup`) - New user registration
- **Dashboard** (`/dashboard`) - User overview and statistics
- **API Keys** (`/dashboard/api-keys`) - Manage API keys
- **Analytics** (`/dashboard/analytics`) - Usage charts and metrics
- **Models** (`/models`) - Browse available models
- **Model Details** (`/models/{id}`) - Detailed model information
- **Providers** (`/providers`) - List of LLM providers

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
