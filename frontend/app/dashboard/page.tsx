"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
} from "@/components/ui/card";
import { paymentsApi, userApi, apiKeysApi } from "@/lib/api";
import {
  Coins,
  Key,
  Loader2,
  ArrowRight,
  BarChart3,
} from "lucide-react";
import { toast } from "sonner";

export default function DashboardPage() {
  const [isOnramping, setIsOnramping] = useState(false);
  const [credits, setCredits] = useState<number>(0);
  const [activeKeyCount, setActiveKeyCount] = useState<number>(0);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      const [profile, keys] = await Promise.all([
        userApi.getProfile(),
        apiKeysApi.getAll(),
      ]);
      setCredits(profile.credits);
      setActiveKeyCount(keys.filter((k) => !k.disabled).length);
    } catch (error) {
      console.error("Failed to load data:", error);
      toast.error("Failed to load dashboard data");
    } finally {
      setIsLoading(false);
    }
  };

  const handleOnramp = async () => {
    setIsOnramping(true);
    try {
      const result = await paymentsApi.onramp();
      setCredits(result.newBalance);
      toast.success(`Added ${result.amount} credits. New balance: ${result.newBalance}`);
    } catch (error) {
      console.error("Failed to onramp:", error);
      toast.error("Failed to add credits");
    } finally {
      setIsOnramping(false);
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Overview</h1>
          <p className="text-muted-foreground mt-1 text-sm">
            Manage your API keys and credits.
          </p>
        </div>
        <Button
          variant="dark"
          size="sm"
          onClick={handleOnramp}
          disabled={isOnramping}
          className="gap-2"
        >
          {isOnramping ? <Loader2 className="h-4 w-4 animate-spin" /> : <Coins className="h-4 w-4" />}
          Add 1000 Credits
        </Button>
      </div>

      <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        <Card className="hover:shadow-md hover:-translate-y-px hover:border-neutral-300 transition-all">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">Credits balance</p>
              <Coins className="h-5 w-5 text-muted-foreground/40" />
            </div>
            <p className="mt-2 text-3xl font-bold tabular-nums">
              {isLoading ? "..." : credits.toLocaleString()}
            </p>
            <p className="mt-1 text-xs text-muted-foreground">
              Updated just now
            </p>
          </CardContent>
        </Card>

        <Card className="hover:shadow-md hover:-translate-y-px hover:border-neutral-300 transition-all">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">Active API Keys</p>
              <Key className="h-5 w-5 text-muted-foreground/40" />
            </div>
            <p className="mt-2 text-3xl font-bold tabular-nums">
              {isLoading ? "..." : activeKeyCount}
            </p>
            <Link
              href="/dashboard/api-keys"
              className="mt-2 inline-flex items-center gap-1 text-xs font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              Manage keys <ArrowRight className="h-3 w-3" />
            </Link>
          </CardContent>
        </Card>

        <Card className="hover:shadow-md hover:-translate-y-px hover:border-neutral-300 transition-all">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">Usage Analytics</p>
              <BarChart3 className="h-5 w-5 text-muted-foreground/40" />
            </div>
            <p className="mt-2 text-3xl font-bold">Track</p>
            <Link
              href="/dashboard/analytics"
              className="mt-2 inline-flex items-center gap-1 text-xs font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              View analytics <ArrowRight className="h-3 w-3" />
            </Link>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
