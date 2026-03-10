"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { analyticsApi } from "@/lib/api";
import { UsageSummary, TimelineDataPoint, CostBreakdown, ApiKeyStats } from "@/types";
import {
  BarChart3,
  TrendingUp,
  Coins,
  Clock,
  CheckCircle,
  Calendar,
  Key,
} from "lucide-react";
import { toast } from "sonner";

type Period = "today" | "7days" | "30days" | "all";

export default function AnalyticsPage() {
  const router = useRouter();
  const { isAuthenticated, isLoading: authLoading } = useAuth();

  const [period, setPeriod] = useState<Period>("30days");
  const [customDateRange, setCustomDateRange] = useState<{ start: string; end: string } | null>(null);
  const [showCustomDate, setShowCustomDate] = useState(false);

  const [summary, setSummary] = useState<UsageSummary | null>(null);
  const [timeline, setTimeline] = useState<TimelineDataPoint[]>([]);
  const [breakdown, setBreakdown] = useState<CostBreakdown | null>(null);
  const [apiKeyStats, setApiKeyStats] = useState<ApiKeyStats[]>([]);

  const [isLoading, setIsLoading] = useState(true);

  // Redirect if not authenticated
  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/");
    }
  }, [isAuthenticated, authLoading, router]);

  useEffect(() => {
    if (isAuthenticated) {
      loadAnalytics();
    }
  }, [period, customDateRange, isAuthenticated]);

  const loadAnalytics = async () => {
    try {
      setIsLoading(true);

      const params = customDateRange
        ? { startDate: customDateRange.start, endDate: customDateRange.end }
        : { period };

      const [summaryData, timelineData, breakdownData, keyStatsData] = await Promise.all([
        analyticsApi.getSummary(params),
        analyticsApi.getTimeline(params),
        analyticsApi.getBreakdown(params),
        analyticsApi.getApiKeyStats(params),
      ]);

      setSummary(summaryData);
      setTimeline(timelineData);
      setBreakdown(breakdownData);
      setApiKeyStats(keyStatsData);
    } catch (error) {
      console.error("Failed to load analytics:", error);
      toast.error("Failed to load analytics data");
    } finally {
      setIsLoading(false);
    }
  };

  const handlePeriodChange = (newPeriod: Period) => {
    setPeriod(newPeriod);
    setCustomDateRange(null);
    setShowCustomDate(false);
  };

  const handleCustomDateApply = (start: string, end: string) => {
    setCustomDateRange({ start, end });
    setShowCustomDate(false);
  };

  const formatCurrency = (amount: number) => {
    return amount.toFixed(4);
  };

  // Show loading spinner while checking auth
  if (authLoading || !isAuthenticated) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Analytics</h1>
          <p className="text-muted-foreground mt-1 text-sm">
            Track your API usage, costs, and performance metrics.
          </p>
        </div>

        {/* Date Range Picker */}
        <div className="flex flex-wrap gap-2">
          <Button
            variant={period === "today" && !customDateRange ? "dark" : "outline"}
            size="sm"
            onClick={() => handlePeriodChange("today")}
          >
            Today
          </Button>
          <Button
            variant={period === "7days" && !customDateRange ? "dark" : "outline"}
            size="sm"
            onClick={() => handlePeriodChange("7days")}
          >
            Last 7 Days
          </Button>
          <Button
            variant={period === "30days" && !customDateRange ? "dark" : "outline"}
            size="sm"
            onClick={() => handlePeriodChange("30days")}
          >
            Last 30 Days
          </Button>
          <Button
            variant={period === "all" && !customDateRange ? "dark" : "outline"}
            size="sm"
            onClick={() => handlePeriodChange("all")}
          >
            All Time
          </Button>
          <Button
            variant={customDateRange ? "dark" : "outline"}
            size="sm"
            onClick={() => setShowCustomDate(!showCustomDate)}
          >
            <Calendar className="h-4 w-4 mr-2" />
            Custom
          </Button>
        </div>
      </div>

      {/* Custom Date Range Input */}
      {showCustomDate && (
        <Card>
          <CardContent className="pt-6">
            <div className="flex flex-col sm:flex-row gap-4 items-end">
              <div className="flex-1 space-y-2">
                <label className="text-sm font-medium">Start Date</label>
                <input
                  type="date"
                  className="w-full px-3 py-2 border rounded-md"
                  onChange={(e) => {
                    const endInput = e.target.parentElement?.parentElement?.querySelector('input[type="date"]:nth-of-type(2)') as HTMLInputElement;
                    if (endInput?.value) {
                      handleCustomDateApply(e.target.value, endInput.value);
                    }
                  }}
                />
              </div>
              <div className="flex-1 space-y-2">
                <label className="text-sm font-medium">End Date</label>
                <input
                  type="date"
                  className="w-full px-3 py-2 border rounded-md"
                  onChange={(e) => {
                    const startInput = e.target.parentElement?.parentElement?.querySelector('input[type="date"]:first-of-type') as HTMLInputElement;
                    if (startInput?.value) {
                      handleCustomDateApply(startInput.value, e.target.value);
                    }
                  }}
                />
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Summary Cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">Total Requests</p>
              <BarChart3 className="h-5 w-5 text-muted-foreground/40" />
            </div>
            <p className="mt-2 text-3xl font-bold tabular-nums">
              {isLoading ? "..." : summary?.totalRequests.toLocaleString() || 0}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">Total Cost (Credits)</p>
              <Coins className="h-5 w-5 text-muted-foreground/40" />
            </div>
            <p className="mt-2 text-3xl font-bold tabular-nums">
              {isLoading ? "..." : formatCurrency(summary?.totalCost || 0)}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">Avg Latency</p>
              <Clock className="h-5 w-5 text-muted-foreground/40" />
            </div>
            <p className="mt-2 text-3xl font-bold tabular-nums">
              {isLoading ? "..." : Math.round(summary?.avgLatency || 0)}
            </p>
            <p className="mt-1 text-xs text-muted-foreground">milliseconds</p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">Success Rate</p>
              <CheckCircle className="h-5 w-5 text-muted-foreground/40" />
            </div>
            <p className="mt-2 text-3xl font-bold tabular-nums">
              {isLoading ? "..." : `${summary?.successRate || 0}%`}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Timeline Chart */}
      <Card>
        <CardHeader>
          <CardTitle>Requests Over Time</CardTitle>
          <CardDescription>Daily request volume and cost</CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="h-64 w-full bg-muted/40 animate-pulse rounded-lg"></div>
          ) : timeline.length === 0 ? (
            <div className="flex items-center justify-center h-64 text-muted-foreground">
              No data available for this period
            </div>
          ) : (
            <div className="space-y-4">
              {timeline.map((point, index) => (
                <div key={index} className="flex items-center gap-4">
                  <div className="w-24 text-sm text-muted-foreground">
                    {new Date(point.date).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <div
                        className="h-8 bg-blue-500 rounded-sm transition-all"
                        style={{
                          width: `${Math.max((point.requestCount / Math.max(...timeline.map(t => t.requestCount))) * 100, 2)}%`
                        }}
                      />
                      <span className="text-sm font-medium tabular-nums">{point.requestCount}</span>
                    </div>
                  </div>
                  <div className="w-24 text-right text-sm font-mono tabular-nums">
                    {formatCurrency(point.totalCost)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      <div className="space-y-4">
        {/* Cost Breakdown by Provider */}
        <Card>
          <CardHeader>
            <CardTitle>Cost by Provider (Credits)</CardTitle>
            <CardDescription>Distribution across LLM providers</CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="h-48 w-full bg-muted/40 animate-pulse rounded-lg"></div>
            ) : !breakdown || breakdown.byProvider.length === 0 ? (
              <div className="flex items-center justify-center h-48 text-muted-foreground">
                No data available
              </div>
            ) : (
              <div className="space-y-3">
                {breakdown.byProvider.map((item, index) => (
                  <div key={index} className="space-y-1">
                    <div className="flex items-center justify-between text-sm">
                      <span className="font-medium">{item.name}</span>
                      <span className="text-muted-foreground">{item.percentage.toFixed(1)}%</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
                        <div
                          className="h-full bg-gradient-to-r from-blue-500 to-blue-600 rounded-full"
                          style={{ width: `${item.percentage}%` }}
                        />
                      </div>
                      <span className="text-xs font-mono tabular-nums text-muted-foreground w-20 text-right">
                        {formatCurrency(item.totalCost)}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Cost Breakdown by Model */}
        <Card>
          <CardHeader>
            <CardTitle>Cost by Model (Credits)</CardTitle>
            <CardDescription>Distribution across AI models</CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="h-48 w-full bg-muted/40 animate-pulse rounded-lg"></div>
            ) : !breakdown || breakdown.byModel.length === 0 ? (
              <div className="flex items-center justify-center h-48 text-muted-foreground">
                No data available
              </div>
            ) : (
              <div className="space-y-3">
                {breakdown.byModel.map((item, index) => (
                  <div key={index} className="space-y-1">
                    <div className="flex items-center justify-between text-sm">
                      <span className="font-medium">{item.name}</span>
                      <span className="text-muted-foreground">{item.percentage.toFixed(1)}%</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
                        <div
                          className="h-full bg-gradient-to-r from-pink-500 to-pink-600 rounded-full"
                          style={{ width: `${item.percentage}%` }}
                        />
                      </div>
                      <span className="text-xs font-mono tabular-nums text-muted-foreground w-20 text-right">
                        {formatCurrency(item.totalCost)}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* API Key Statistics */}
      <Card>
        <CardHeader>
          <CardTitle>Usage by API Key (Credits)</CardTitle>
          <CardDescription>Cost and request breakdown per API key</CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {[1, 2, 3].map(i => (
                <div key={i} className="h-16 w-full bg-muted/40 animate-pulse rounded-lg"></div>
              ))}
            </div>
          ) : apiKeyStats.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 text-muted-foreground">
              <Key className="h-12 w-12 mb-3 opacity-20" />
              <p>No API key usage for this period</p>
            </div>
          ) : (
            <div className="space-y-4">
              {apiKeyStats.map((stat) => (
                <div
                  key={stat.apiKeyId}
                  className="flex items-center justify-between p-4 rounded-lg border hover:bg-muted/30 transition-colors"
                >
                  <div className="flex-1">
                    <h4 className="font-semibold">{stat.apiKeyName}</h4>
                    <p className="text-sm text-muted-foreground mt-1">
                      {stat.requestCount.toLocaleString()} requests
                      {stat.lastUsed && (
                        <> • Last used {new Date(stat.lastUsed).toLocaleDateString()}</>
                      )}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-lg font-bold font-mono tabular-nums">
                      {formatCurrency(stat.totalCost)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
