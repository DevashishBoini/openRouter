"use client";

import { useEffect, useState } from "react";
import { modelsApi } from "@/lib/api";
import { Model } from "@/types";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Navbar } from "@/components/Navbar";
import { Search, ChevronRight } from "lucide-react";
import Link from "next/link";

export default function ModelsPage() {
  const [models, setModels] = useState<Model[]>([]);
  const [filteredModels, setFilteredModels] = useState<Model[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    loadModels();
  }, []);

  useEffect(() => {
    if (searchQuery.trim() === "") {
      setFilteredModels(models);
    } else {
      const query = searchQuery.toLowerCase();
      const filtered = models.filter(
        (model) =>
          model.name.toLowerCase().includes(query) ||
          model.slug.toLowerCase().includes(query) ||
          model.companyName.toLowerCase().includes(query)
      );
      setFilteredModels(filtered);
    }
  }, [searchQuery, models]);

  const loadModels = async () => {
    try {
      const data = await modelsApi.getAll();
      setModels(data);
      setFilteredModels(data);
    } catch (error) {
      console.error("Failed to load models:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background font-sans">
      <Navbar />

      <main className="w-full max-w-3xl mx-auto px-6 py-8">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
          <div>
            <h1 className="text-2xl font-bold tracking-tight">Models</h1>
            <p className="text-sm text-muted-foreground mt-0.5">
              {filteredModels.length} model{filteredModels.length !== 1 ? "s" : ""} available
            </p>
          </div>
          <div className="relative w-full sm:w-72">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              className="pl-9"
              placeholder="Search models..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>

        {isLoading ? (
          <div className="space-y-2.5">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="rounded-xl border bg-card p-5">
                <div className="h-4 w-48 bg-muted animate-pulse rounded" />
                <div className="h-3 w-36 bg-muted animate-pulse rounded mt-2.5" />
              </div>
            ))}
          </div>
        ) : filteredModels.length === 0 ? (
          <Card className="border-dashed">
            <CardContent className="flex flex-col items-center justify-center py-16 text-center">
              <Search className="h-10 w-10 text-muted-foreground/40 mb-4" />
              <h3 className="font-semibold mb-1">No models found</h3>
              <p className="text-sm text-muted-foreground max-w-sm">
                No models match &quot;{searchQuery}&quot;.
              </p>
            </CardContent>
          </Card>
        ) : (
          <ul className="space-y-2.5">
            {filteredModels.map((model) => (
              <li key={model.id}>
                <Link href={`/models/${model.id}`} className="block group">
                  <Card className="cursor-pointer hover:shadow-md hover:-translate-y-px hover:border-primary/30">
                    <CardContent className="flex items-center justify-between gap-4 px-5 py-4">
                      <div className="min-w-0 flex-1">
                        <p className="font-semibold truncate group-hover:text-primary transition-colors">
                          {model.name}
                        </p>
                        <p className="text-sm text-muted-foreground font-mono truncate mt-0.5">
                          {model.slug} · {model.companyName}
                        </p>
                      </div>
                      <ChevronRight className="h-5 w-5 shrink-0 text-muted-foreground/30 group-hover:text-primary group-hover:translate-x-0.5 transition-all" />
                    </CardContent>
                  </Card>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </main>
    </div>
  );
}
