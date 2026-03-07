"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Navbar } from "@/components/Navbar";
import Link from "next/link";
import { ArrowRight } from "lucide-react";

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      router.push("/dashboard");
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading) {
    return null;
  }

  return (
    <div className="min-h-screen flex flex-col bg-background font-sans">
      <Navbar />

      <main className="flex-1">
        <section className="relative px-6 py-24 lg:py-36 overflow-hidden">
          <div className="w-full max-w-5xl mx-auto flex flex-col items-center text-center">
            <div className="space-y-8 max-w-2xl">
              <h1 className="text-5xl lg:text-6xl font-bold tracking-tight leading-[1.1]">
                One API.<br />
                <span className="text-primary">All Models.</span>
              </h1>

              <p className="text-lg text-muted-foreground max-w-lg mx-auto leading-relaxed">
                Access the world&apos;s best AI models through a unified interface. No more managing multiple API keys or navigating complex documentation.
              </p>

              <div className="flex items-center gap-3 justify-center">
                <Link href="/signup">
                  <Button variant="dark" size="lg" className="h-12 px-8 text-base">
                    Get Your API Key <ArrowRight className="ml-2 h-4 w-4" />
                  </Button>
                </Link>
                <Link href="/models">
                  <Button variant="outline" size="lg" className="h-12 px-8 text-base">
                    Browse Models
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}
