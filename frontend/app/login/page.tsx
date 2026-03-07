"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent } from "@/components/ui/card";
import { Navbar } from "@/components/Navbar";
import Link from "next/link";
import { ApiError } from "@/lib/api";
import { toast } from "sonner";
import { Loader2 } from "lucide-react";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      await login({ email, password });
      toast.success("Welcome back!");
      router.push("/dashboard");
    } catch (err) {
      if (err instanceof ApiError) {
        toast.error(err.message);
        setError(err.message);
      } else {
        toast.error("An unexpected error occurred");
        setError("An unexpected error occurred");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <main className="w-full max-w-3xl mx-auto px-6 py-16 flex flex-col items-center">
        <div className="w-full max-w-sm">
          <div className="space-y-2 text-center mb-8">
            <h1 className="text-3xl font-bold tracking-tight">Login</h1>
            <p className="text-sm text-muted-foreground">
              Enter your credentials to access your dashboard
            </p>
          </div>

          <Card>
            <CardContent className="p-6 pt-6">
              <form onSubmit={handleSubmit} className="space-y-5">
                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                      disabled={isLoading}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="password">Password</Label>
                    <Input
                      id="password"
                      type="password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                      disabled={isLoading}
                    />
                  </div>
                </div>

                {error && (
                  <div className="p-3 rounded-lg bg-destructive/10 text-destructive text-sm font-medium border border-destructive/20">
                    {error}
                  </div>
                )}

                <Button type="submit" variant="dark" className="w-full h-10" disabled={isLoading}>
                  {isLoading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Signing in...
                    </>
                  ) : (
                    "Login"
                  )}
                </Button>
              </form>
            </CardContent>
          </Card>

          <p className="text-center text-sm text-muted-foreground mt-6">
            Don&apos;t have an account?{" "}
            <Link href="/signup" className="font-semibold text-foreground hover:underline underline-offset-4 transition-colors">
              Sign up
            </Link>
          </p>
        </div>
      </main>
    </div>
  );
}
