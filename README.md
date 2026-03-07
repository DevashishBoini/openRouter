# OpenRouter

AI Model Router with authentication, API key management, and multi-provider support. Monorepo containing Spring Boot backend and Next.js frontend with separate servers.

## 🏗️ Project Structure

```
openRouter/
├── backend/          # Spring Boot REST API (Port 8080)
│   ├── src/
│   ├── pom.xml
│   └── README.md
│
└── frontend/         # Next.js Frontend (Port 3000)
    ├── app/
    ├── components/
    ├── package.json
    └── README.md
```

## ⚡ Quick Start

### Prerequisites
- **Backend:** Java 17+, IntelliJ IDEA, PostgreSQL
- **Frontend:** Node.js 18+, npm

### 1. Start Backend (Port 8080)

```bash
cd backend

# Configure database in src/main/resources/application-local.properties
# Then run in IntelliJ:
# Open MainApplication.java → Click green ▶️ button
```

Backend runs on: **http://localhost:8080**

### 2. Start Frontend (Port 3000)

```bash
cd frontend

# Install dependencies
npm install

# Create .env.local
cp .env.local.example .env.local

# Start development server
npm run dev
```

Frontend runs on: **http://localhost:3000**

## 🚀 Features

### Backend API
- ✅ **JWT Authentication** - Secure token-based auth
- ✅ **API Key Management** - Create, disable, delete keys
- ✅ **Model/Provider Management** - Browse AI models & providers
- ✅ **Credit System** - Onramp credits for API usage
- ✅ **Request Tracing** - Unique trace IDs for debugging
- ✅ **Global Exception Handling** - Consistent error responses

### Frontend UI
- ✅ **Authentication Pages** - Signup, login, logout
- ✅ **Dashboard** - Overview with credits & model stats
- ✅ **API Key Management** - Full CRUD with usage tracking
- ✅ **Responsive Design** - Mobile-friendly interface
- ✅ **shadcn/ui Components** - Beautiful, accessible UI
- ✅ **TypeScript** - Full type safety

## 📚 Documentation

### Backend
- **Full README:** [backend/README.md](./backend/README.md)
- **API Documentation:** See backend README for all endpoints
- **Tech Stack:** Spring Boot, PostgreSQL, JWT, MapStruct

### Frontend
- **Full README:** [frontend/README.md](./frontend/README.md)
- **Component Library:** shadcn/ui with Tailwind CSS
- **Tech Stack:** Next.js 16, TypeScript, React Context API

## 🔌 API Endpoints

### Public (No Auth Required)
- `POST /v1/auth/signup` - Create account
- `POST /v1/auth/login` - Login
- `GET /api/v1/models` - List all models
- `GET /api/v1/providers` - List all providers
- `GET /api/v1/models/{id}/providers` - Get providers for model

### Protected (Requires JWT Token)
- `POST /api/v1/api-keys/create` - Create API key
- `GET /api/v1/api-keys` - Get user's API keys
- `PATCH /api/v1/api-keys/disable/{id}` - Enable/disable key
- `PATCH /api/v1/api-keys/delete/{id}` - Delete key
- `POST /api/v1/payments/onramp` - Add 1000 credits

## 🔧 Development

### Running Both Servers

**Terminal 1 - Backend:**
```bash
cd backend
# Run in IntelliJ or use Maven
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```

### Architecture

```
Frontend (3000) → Backend API (8080) → PostgreSQL Database
     ↓                    ↓
 React/Next.js     Spring Boot/JWT
  TypeScript         Java 17
```

### Key Technologies

**Backend:**
- Spring Boot 3.2.3
- Spring Security + JWT
- PostgreSQL + JPA/Hibernate
- Lombok, MapStruct

**Frontend:**
- Next.js 16 (App Router)
- TypeScript
- Tailwind CSS + shadcn/ui
- React Context API

## 🌐 Environment Configuration

### Backend (.env or application-local.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your-password
jwt.secret=your-secret-key
jwt.expiration=3600000  # 1 hour
```

### Frontend (.env.local)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## 📦 Deployment

### Backend
- Build: `mvn clean package`
- Run JAR: `java -jar target/backend-0.0.1-SNAPSHOT.jar`
- Port: 8080

### Frontend
- Build: `npm run build`
- Start: `npm start`
- Port: 3000

## 🛠️ Troubleshooting

### Backend Issues
- **Port 8080 in use:** Change in `application.properties`
- **Database connection:** Verify PostgreSQL is running
- **JWT errors:** Check secret key configuration

### Frontend Issues
- **Can't connect to API:** Ensure backend is running on 8080
- **Build errors:** Delete `.next` folder and rebuild
- **Auth not working:** Clear localStorage and re-login

## 📝 Notes

- **JWT Expiration:** 1 hour (3600000 ms)
- **Onramp Amount:** Fixed 1000 credits per transaction
- **API Keys:** Shown only once during creation - save securely
- **Soft Deletes:** API keys marked as deleted, not removed
- **Models/Providers:** Admin-managed in database (no API endpoints)

## 🔮 Future Enhancements

- [ ] Razorpay payment integration
- [ ] Rate limiting & throttling
- [ ] API usage analytics dashboard
- [ ] Email verification
- [ ] Password reset flow
- [ ] Dark mode toggle
- [ ] Real-time usage monitoring
- [ ] Team collaboration
- [ ] Webhook configuration

## 📄 License

MIT
