"use client";

import { useEffect, useState } from "react";
import { providersApi } from "@/lib/api";
import { Provider } from "@/types";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Navbar } from "@/components/Navbar";
import { Search, ExternalLink, Building2, Server } from "lucide-react";
import { toast } from "sonner";

export default function ProvidersPage() {
  const [providers, setProviders] = useState<Provider[]>([]);
  const [filteredProviders, setFilteredProviders] = useState<Provider[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    loadProviders();
  }, []);

  useEffect(() => {
    if (searchQuery.trim() === "") {
      setFilteredProviders(providers);
    } else {
      const query = searchQuery.toLowerCase();
      const filtered = providers.filter(
        (provider) =>
          provider.name.toLowerCase().includes(query) ||
          provider.website.toLowerCase().includes(query)
      );
      setFilteredProviders(filtered);
    }
  }, [searchQuery, providers]);

  const loadProviders = async () => {
    try {
      const data = await providersApi.getAll();
      setProviders(data);
      setFilteredProviders(data);
    } catch (error) {
      console.error("Failed to load providers:", error);
      toast.error("Failed to load providers");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <main className="w-full max-w-3xl mx-auto px-6 py-8">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
          <div>
            <h1 className="text-2xl font-bold tracking-tight">Providers</h1>
            <p className="text-sm text-muted-foreground mt-0.5">
              Showing {filteredProviders.length} provider{filteredProviders.length !== 1 ? "s" : ""}
            </p>
          </div>
          <div className="relative w-full sm:w-72">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              className="pl-9"
              placeholder="Search providers..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {[1, 2, 3, 4].map((i) => (
              <Card key={i}>
                <CardContent className="p-6">
                  <div className="h-10 w-10 rounded-lg bg-muted animate-pulse mb-4" />
                  <div className="h-5 w-32 bg-muted animate-pulse rounded" />
                </CardContent>
              </Card>
            ))}
          </div>
        ) : filteredProviders.length === 0 ? (
          <Card className="border-dashed">
            <CardContent className="flex flex-col items-center justify-center py-16 text-center">
              <Server className="h-10 w-10 text-muted-foreground/40 mb-4" />
              <h3 className="font-semibold mb-1">No providers found</h3>
              <p className="text-sm text-muted-foreground max-w-sm mb-4">
                No providers match &quot;{searchQuery}&quot;.
              </p>
              <Button variant="outline" size="sm" onClick={() => setSearchQuery("")}>
                Clear search
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {filteredProviders.map((provider) => (
              <Card
                key={provider.id}
                className="hover:shadow-md hover:-translate-y-px hover:border-primary/30"
              >
                <CardContent className="p-6">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary mb-4">
                    <Building2 className="h-5 w-5" />
                  </div>
                  <h3 className="font-semibold text-base mb-1.5">{provider.name}</h3>
                  {provider.website && (
                    <a
                      href={provider.website}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center gap-1 text-xs text-muted-foreground hover:text-primary hover:underline transition-colors"
                    >
                      {provider.website.replace(/^https?:\/\//, "").replace(/\/$/, "")}
                      <ExternalLink className="h-3 w-3" />
                    </a>
                  )}
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
