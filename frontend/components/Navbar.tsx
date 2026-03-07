"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { cn } from "@/lib/utils";
import { useAuth } from "@/contexts/AuthContext";
import { userApi } from "@/lib/api";
import { Cpu, LogOut, ChevronDown, User } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
} from "@/components/ui/dropdown-menu";

function NavLink({ href, label }: { href: string; label: string }) {
  const pathname = usePathname();
  const isActive =
    href === "/dashboard"
      ? pathname === "/dashboard"
      : pathname === href || pathname.startsWith(href + "/");

  return (
    <Link
      href={href}
      className={cn(
        "relative px-4 py-2.5 rounded-md text-[13px] leading-none font-medium transition-all duration-150",
        isActive
          ? "bg-neutral-900 text-white"
          : "text-muted-foreground hover:text-foreground [&:hover>.nav-bar]:opacity-100"
      )}
    >
      {label}
      {!isActive && (
        <span className="nav-bar absolute bottom-0 left-2 right-2 h-[2px] bg-neutral-900 rounded-full opacity-0 transition-opacity duration-150" />
      )}
    </Link>
  );
}

function AuthenticatedNav() {
  const { logout } = useAuth();
  const router = useRouter();
  const [userEmail, setUserEmail] = useState<string>("");
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    userApi.getProfile().then((p) => setUserEmail(p.email)).catch(() => {});
  }, []);

  const handleLogout = () => {
    logout();
    router.push("/");
  };

  const navItems = [
    { href: "/dashboard", label: "Overview" },
    { href: "/dashboard/api-keys", label: "API Keys" },
    { href: "/models", label: "Models" },
    { href: "/providers", label: "Providers" },
  ];

  return (
    <>
      <div className="flex items-center gap-8 min-w-0">
        <Link href="/dashboard" className="flex items-center gap-2.5 shrink-0 group">
          <div className="h-7 w-7 bg-neutral-900 rounded-lg flex items-center justify-center text-white transition-transform group-hover:scale-105">
            <Cpu className="h-4 w-4" />
          </div>
          <span className="font-semibold text-[15px] leading-none tracking-tight">OpenRouter</span>
        </Link>

        <nav className="hidden sm:flex items-center gap-1">
          {navItems.map((item) => (
            <NavLink key={item.href} {...item} />
          ))}
        </nav>
      </div>

      <DropdownMenu open={menuOpen} onOpenChange={setMenuOpen}>
        <DropdownMenuTrigger asChild>
          <button className="flex items-center gap-2 outline-none">
            <div className="h-7 w-7 rounded-full bg-neutral-900 flex items-center justify-center text-white text-[11px] font-semibold shrink-0">
              {userEmail ? userEmail[0].toUpperCase() : <User className="h-3 w-3" />}
            </div>
            <div
              className={cn(
                "h-7 w-7 rounded-md border flex items-center justify-center transition-all duration-150",
                menuOpen
                  ? "bg-neutral-900 border-neutral-900 text-white"
                  : "border-neutral-300 text-neutral-600 hover:bg-neutral-900 hover:border-neutral-900 hover:text-white"
              )}
            >
              <ChevronDown
                className={cn(
                  "h-3.5 w-3.5 transition-transform duration-150",
                  menuOpen && "rotate-180"
                )}
              />
            </div>
          </button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-60 p-0">
          <div className="px-4 py-3 border-b border-neutral-100">
            <p className="text-[11px] uppercase tracking-wider text-muted-foreground font-medium">Account</p>
            <p className="text-sm font-semibold truncate mt-1">{userEmail || "..."}</p>
          </div>
          <div className="px-3 py-2">
            <button
              onClick={handleLogout}
              className="inline-flex items-center gap-2 rounded-md bg-neutral-900 text-white px-3 py-1.5 text-[13px] font-medium hover:bg-neutral-800 transition-colors cursor-pointer"
            >
              <LogOut className="h-3.5 w-3.5" />
              Sign Out
            </button>
          </div>
        </DropdownMenuContent>
      </DropdownMenu>
    </>
  );
}

function PublicNav() {
  const publicLeft = [
    { href: "/models", label: "Models" },
    { href: "/providers", label: "Providers" },
  ];

  const publicRight = [
    { href: "/login", label: "Login" },
    { href: "/signup", label: "Sign Up" },
  ];

  return (
    <>
      <div className="flex items-center gap-8 min-w-0">
        <Link href="/" className="flex items-center gap-2.5 shrink-0 group">
          <div className="h-7 w-7 bg-neutral-900 rounded-lg flex items-center justify-center text-white transition-transform group-hover:scale-105">
            <Cpu className="h-4 w-4" />
          </div>
          <span className="font-semibold text-[15px] leading-none tracking-tight">OpenRouter</span>
        </Link>

        <nav className="hidden sm:flex items-center gap-1">
          {publicLeft.map((link) => (
            <NavLink key={link.href} {...link} />
          ))}
        </nav>
      </div>

      <div className="flex items-center gap-1 shrink-0">
        {publicRight.map((link) => (
          <NavLink key={link.href} {...link} />
        ))}
      </div>
    </>
  );
}

export function Navbar() {
  const { isAuthenticated, isLoading } = useAuth();

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-card">
      <div className="w-full max-w-5xl mx-auto px-6 h-14 flex items-center justify-between">
        {isLoading ? (
          <Link href="/" className="flex items-center gap-2.5 shrink-0">
            <div className="h-7 w-7 bg-neutral-900 rounded-lg flex items-center justify-center text-white">
              <Cpu className="h-4 w-4" />
            </div>
            <span className="font-semibold text-[15px] leading-none tracking-tight">OpenRouter</span>
          </Link>
        ) : isAuthenticated ? (
          <AuthenticatedNav />
        ) : (
          <PublicNav />
        )}
      </div>
    </header>
  );
}
