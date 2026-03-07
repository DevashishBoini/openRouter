"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { modelsApi } from "@/lib/api";
import { Model, ModelProvider } from "@/types";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Navbar } from "@/components/Navbar";
import { Building2, ExternalLink, Loader2, Sparkles, ArrowLeft, Copy } from "lucide-react";
import { toast } from "sonner";

export default function ModelDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const [model, setModel] = useState<Model | null>(null);
  const [providers, setProviders] = useState<ModelProvider[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [providersLoading, setProvidersLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    const load = async () => {
      try {
        setIsLoading(true);
        const all = await modelsApi.getAll();
        const found = all.find((m) => m.id === id);
        setModel(found ?? null);
      } catch (error) {
        console.error("Failed to load model:", error);
        toast.error("Failed to load model");
        setModel(null);
      } finally {
        setIsLoading(false);
      }
    };
    load();
  }, [id]);

  useEffect(() => {
    if (!id || !model) return;
    let cancelled = false;
    setProvidersLoading(true);
    modelsApi
      .getProviders(id)
      .then((data) => {
        if (!cancelled) setProviders(data);
      })
      .catch((error) => {
        if (!cancelled) {
          console.error("Failed to load providers:", error);
          toast.error("Failed to load providers");
        }
      })
      .finally(() => {
        if (!cancelled) setProvidersLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [id, model]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex items-center justify-center py-32">
          <div className="flex flex-col items-center gap-3">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
            <span className="text-sm text-muted-foreground">Loading model…</span>
          </div>
        </div>
      </div>
    );
  }

  if (!model) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex flex-col items-center justify-center py-32 gap-6">
          <Card>
            <CardContent className="flex flex-col items-center gap-4 py-12 px-8">
              <p className="text-muted-foreground">Model not found</p>
              <Link href="/models">
                <Button variant="outline" className="gap-2">
                  <ArrowLeft className="h-4 w-4" /> Back to models
                </Button>
              </Link>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <main className="w-full max-w-3xl mx-auto px-6 py-8">
        <Link href="/models" className="inline-flex items-center gap-1.5 text-sm text-muted-foreground hover:text-primary transition-colors mb-6">
          <ArrowLeft className="h-3.5 w-3.5" /> Back to models
        </Link>

        <Card className="mb-10 overflow-hidden border-primary/20">
          <CardContent className="p-7 sm:p-8">
            <div className="flex items-start gap-4">
              <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
                <Sparkles className="h-7 w-7" />
              </div>
              <div className="min-w-0 flex-1">
                <Badge className="font-mono text-xs mb-2 bg-neutral-900 text-white hover:bg-neutral-800">
                  {model.companyName}
                </Badge>
                <h1 className="text-2xl font-bold tracking-tight sm:text-3xl">{model.name}</h1>
                <div className="mt-1 flex items-center gap-2">
                  <p className="font-mono text-sm text-muted-foreground">{model.slug}</p>
                  <button
                    onClick={() => {
                      navigator.clipboard.writeText(model.slug);
                      toast.success("Slug copied!");
                    }}
                    className="inline-flex items-center justify-center h-6 w-6 rounded-md border border-neutral-200 text-muted-foreground hover:bg-neutral-900 hover:text-white hover:border-neutral-900 transition-all"
                    title="Copy slug"
                  >
                    <Copy className="h-3 w-3" />
                  </button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <section>
          <div className="mb-5">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold tracking-tight">Providers & Pricing</h2>
              {!providersLoading && providers.length > 0 && (
                <span className="text-sm text-muted-foreground">
                  {providers.length} provider{providers.length !== 1 ? "s" : ""}
                </span>
              )}
            </div>
            <p className="text-sm font-medium text-muted-foreground mt-1">Cost per 1 million tokens</p>
          </div>

          {providersLoading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {[1, 2].map((i) => (
                <Card key={i}>
                  <CardContent className="p-6">
                    <div className="h-10 w-10 rounded-lg bg-muted animate-pulse mb-4" />
                    <div className="h-5 w-32 bg-muted animate-pulse rounded" />
                    <div className="h-4 w-24 bg-muted animate-pulse rounded mt-4" />
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : providers.length === 0 ? (
            <Card className="border-dashed">
              <CardContent className="flex flex-col items-center justify-center py-16 text-center">
                <Building2 className="h-10 w-10 text-muted-foreground/40 mb-4" />
                <p className="text-sm text-muted-foreground">No provider data for this model.</p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {providers.map((p) => (
                <Card
                  key={p.providerId}
                  className="group hover:shadow-md hover:-translate-y-px hover:border-primary/30 transition-all"
                >
                  <CardHeader className="pb-3">
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
                        <Building2 className="h-5 w-5" />
                      </div>
                      {p.providerWebsite && (
                        <a
                          href={p.providerWebsite}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1 rounded-md px-2 py-1 text-xs font-medium text-muted-foreground hover:text-primary hover:bg-primary/5 transition-colors"
                        >
                          Visit <ExternalLink className="h-3 w-3" />
                        </a>
                      )}
                    </div>
                    <CardTitle className="text-base mt-3">{p.providerName}</CardTitle>
                  </CardHeader>
                  <CardContent className="border-t pt-4">
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <span className="text-[11px] font-medium uppercase tracking-wider text-muted-foreground">Input</span>
                        <p className="font-mono text-base font-semibold tabular-nums mt-0.5">${p.inputTokenCost}/M</p>
                      </div>
                      <div>
                        <span className="text-[11px] font-medium uppercase tracking-wider text-muted-foreground">Output</span>
                        <p className="font-mono text-base font-semibold tabular-nums mt-0.5">${p.outputTokenCost}/M</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
