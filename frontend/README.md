# OpenRouter Frontend

Next.js frontend application for OpenRouter - AI Model Router with authentication, API key management, and model browsing.

## Tech Stack

- **Next.js 16** - React framework with App Router
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **shadcn/ui** - UI component library
- **React Context API** - State management

## Prerequisites

- Node.js 18+ and npm
- Backend API running on `http://localhost:8080`

## Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Configure Environment

Create `.env.local` file:

```bash
cp .env.local.example .env.local
```

Update if needed:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

### 3. Run Development Server

```bash
npm run dev
```

The app will run on **http://localhost:3000**

### 4. Build for Production

```bash
npm run build
npm start
```

## Project Structure

```
frontend/
├── app/                          # Next.js App Router
│   ├── layout.tsx                # Root layout with AuthProvider
│   ├── page.tsx                  # Landing page
│   ├── globals.css               # Global styles + Tailwind
│   ├── login/
│   │   └── page.tsx              # Login page
│   ├── signup/
│   │   └── page.tsx              # Signup page
│   └── dashboard/
│       ├── layout.tsx            # Dashboard layout with nav
│       ├── page.tsx              # Dashboard overview
│       └── api-keys/
│           └── page.tsx          # API key management
│
├── components/
│   └── ui/                       # shadcn/ui components
│       ├── button.tsx
│       ├── card.tsx
│       ├── input.tsx
│       └── label.tsx
│
├── contexts/
│   └── AuthContext.tsx           # Authentication state management
│
├── lib/
│   ├── api.ts                    # Backend API client
│   └── utils.ts                  # Utility functions (cn)
│
├── types/
│   └── index.ts                  # TypeScript type definitions
│
└── hooks/                        # Custom React hooks (future)
```

## Features

### 🔐 Authentication
- **Signup** - Create new account
- **Login** - JWT-based authentication
- **Logout** - Clear session
- **Protected Routes** - Dashboard requires authentication

### 🔑 API Key Management
- **Create API Keys** - Generate new keys with custom names
- **View API Keys** - See all your keys with usage stats
- **Enable/Disable** - Toggle key status without deletion
- **Delete Keys** - Soft delete keys (permanent)
- **Copy to Clipboard** - Easy key copying
- **Usage Tracking** - See credits consumed and last used

### 📊 Dashboard
- **Credits Display** - View current credit balance
- **Onramp Credits** - Add 1000 credits (test feature)
- **Models Overview** - Browse available AI models
- **Providers Overview** - See connected AI providers
- **Model Details** - View model companies and slugs

### 🎨 UI/UX
- **Responsive Design** - Mobile-friendly interface
- **Dark Mode Support** - Built-in with shadcn/ui
- **Loading States** - Proper loading indicators
- **Error Handling** - User-friendly error messages
- **Toast Notifications** - Success/error feedback

## API Integration

The frontend connects to the backend API at `http://localhost:8080`:

### Public Endpoints (No Auth)
- `POST /v1/auth/signup` - User signup
- `POST /v1/auth/login` - User login
- `GET /api/v1/models` - Get all models
- `GET /api/v1/providers` - Get all providers
- `GET /api/v1/models/{id}/providers` - Get providers for model

### Protected Endpoints (Requires JWT)
- `POST /api/v1/api-keys/create` - Create API key
- `GET /api/v1/api-keys` - Get user's API keys
- `PATCH /api/v1/api-keys/disable/{id}` - Enable/disable key
- `PATCH /api/v1/api-keys/delete/{id}` - Delete key
- `POST /api/v1/payments/onramp` - Add credits

## State Management

### AuthContext
- **isAuthenticated**: Boolean - User login status
- **userId**: String | null - Current user ID
- **login()**: Async function - Login user
- **signup()**: Async function - Signup user
- **logout()**: Function - Logout user
- **isLoading**: Boolean - Initial auth check loading

Usage:
```tsx
import { useAuth } from "@/contexts/AuthContext";

const { isAuthenticated, login, logout } = useAuth();
```

## Components

### shadcn/ui Components
- **Button** - Various variants (default, outline, ghost, etc.)
- **Card** - Container with header, content, footer
- **Input** - Form input with validation styles
- **Label** - Form labels

All components support:
- TypeScript types
- Tailwind CSS variants
- Accessibility features
- Responsive design

## Styling

### Tailwind CSS
- Utility-first CSS framework
- Custom color scheme via CSS variables
- Responsive breakpoints
- Dark mode support

### Color Scheme
Colors defined in `app/globals.css` using HSL values:
- Primary, Secondary, Accent
- Destructive (errors/danger)
- Muted (subtle text/backgrounds)
- Border, Input, Ring

Customize in `globals.css` `:root` and `.dark` sections.

## Development Tips

### Adding New Pages
1. Create file in `app/` directory
2. Export default React component
3. Add route to navigation if needed

### Adding shadcn/ui Components
Components are manually added from [shadcn/ui documentation](https://ui.shadcn.com/).

### API Calls
Use the API client in `lib/api.ts`:
```tsx
import { apiKeysApi } from "@/lib/api";

const keys = await apiKeysApi.getAll();
```

### Protected Routes
Wrap with auth check:
```tsx
"use client";
import { useAuth } from "@/contexts/AuthContext";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function ProtectedPage() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push("/login");
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading || !isAuthenticated) return <div>Loading...</div>;

  return <div>Protected Content</div>;
}
```

## Scripts

```bash
# Development
npm run dev          # Start dev server on localhost:3000

# Production
npm run build        # Build for production
npm start            # Start production server

# Code Quality
npm run lint         # Run ESLint
```

## Environment Variables

### `.env.local` (Not committed)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

### `.env.local.example` (Template)
Committed to repo as template for other developers.

## Deployment

### Vercel (Recommended)
1. Push code to GitHub
2. Import project in Vercel
3. Add environment variable `NEXT_PUBLIC_API_URL`
4. Deploy

### Docker
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Future Enhancements

- [ ] Model pricing comparison view
- [ ] API usage analytics dashboard
- [ ] Dark mode toggle
- [ ] Email verification
- [ ] Password reset flow
- [ ] User profile management
- [ ] Real-time usage monitoring
- [ ] Webhook configuration
- [ ] Team collaboration features
- [ ] Billing history

## Troubleshooting

### API Connection Issues
- Ensure backend is running on port 8080
- Check `NEXT_PUBLIC_API_URL` in `.env.local`
- Verify CORS settings in backend

### Authentication Issues
- Clear localStorage: `localStorage.clear()`
- Check JWT token expiration (1 hour)
- Verify backend JWT secret is configured

### Build Errors
- Delete `.next` folder and rebuild
- Clear node_modules: `rm -rf node_modules && npm install`
- Check TypeScript errors: `npm run build`

## License

MIT
