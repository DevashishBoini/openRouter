"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { apiKeysApi } from "@/lib/api";
import { ApiKey } from "@/types";
import { Copy, Trash2, Plus, Key, ShieldAlert, Activity } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Switch } from "@/components/ui/switch";

export const dynamic = "force-dynamic";

export default function ApiKeysPage() {
  const [apiKeys, setApiKeys] = useState<ApiKey[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [newKeyName, setNewKeyName] = useState("");
  const [createdKey, setCreatedKey] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<ApiKey | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    loadApiKeys();
  }, []);

  const loadApiKeys = async () => {
    try {
      setIsLoading(true);
      const keys = await apiKeysApi.getAll();
      setApiKeys(keys);
    } catch (error) {
      console.error("Failed to load API keys:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newKeyName.trim()) return;
    
    setIsCreating(true);

    try {
      const result = await apiKeysApi.create({ apiKeyName: newKeyName });
      setCreatedKey(result.apiKeyValue);
      setNewKeyName("");
      toast.success("API key created successfully!");
      await loadApiKeys();
    } catch (error) {
      console.error("Failed to create API key:", error);
      toast.error("Failed to create API key");
    } finally {
      setIsCreating(false);
    }
  };

  const handleCloseDialog = () => {
    setIsCreateDialogOpen(false);
    setCreatedKey(null);
    setNewKeyName("");
  };

  const handleToggleDisabled = async (id: string, currentDisabled: boolean) => {
    try {
      await apiKeysApi.updateDisabled(id, { disabled: !currentDisabled });
      toast.success(`API key ${currentDisabled ? "enabled" : "disabled"} successfully`);
      
      // Optimistic update
      setApiKeys(apiKeys.map(key => 
        key.id === id ? { ...key, disabled: !currentDisabled } : key
      ));
    } catch (error) {
      console.error("Failed to update API key:", error);
      toast.error("Failed to update API key");
      await loadApiKeys(); // Revert on failure
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await apiKeysApi.delete(deleteTarget.id);
      setApiKeys(apiKeys.filter(key => key.id !== deleteTarget.id));
      toast.success("API key deleted successfully");
    } catch (error) {
      console.error("Failed to delete API key:", error);
      toast.error("Failed to delete API key");
    } finally {
      setIsDeleting(false);
      setDeleteTarget(null);
    }
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    toast.success("Copied to clipboard!");
  };

  return (
    <div className="space-y-8">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">API Keys</h1>
          <p className="text-muted-foreground mt-2">
            Manage your API keys for authentication and usage tracking.
          </p>
        </div>
        
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button variant="dark" className="shadow-sm">
              <Plus className="mr-2 h-4 w-4" />
              Create New Key
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle>Create API Key</DialogTitle>
              <DialogDescription>
                Generates a new API key for your applications.
              </DialogDescription>
            </DialogHeader>
            
            {!createdKey ? (
              <form onSubmit={handleCreate} className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="keyName">Key Name</Label>
                  <Input
                    id="keyName"
                    placeholder="e.g. Production App, Test Environment"
                    value={newKeyName}
                    onChange={(e) => setNewKeyName(e.target.value)}
                    required
                    autoFocus
                  />
                  <p className="text-xs text-muted-foreground">
                    Give your key a recognizable name to track usage later.
                  </p>
                </div>
                <DialogFooter>
                  <Button type="button" variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                    Cancel
                  </Button>
                  <Button type="submit" variant="dark" disabled={isCreating || !newKeyName.trim()}>
                     {isCreating && <Activity className="mr-2 h-4 w-4 animate-spin" />}
                     Create Key
                  </Button>
                </DialogFooter>
              </form>
            ) : (
              <div className="space-y-4 py-4 overflow-hidden">
                <div className="rounded-md p-4 border border-neutral-200 bg-neutral-50">
                  <Label className="text-sm font-medium mb-2 block">Your new API key</Label>
                  <div className="flex items-center gap-2">
                    <code className="flex-1 rounded bg-white p-2 font-mono text-sm border border-neutral-200 overflow-x-auto whitespace-nowrap block">
                      {createdKey}
                    </code>
                    <Button
                      size="icon"
                      variant="outline"
                      onClick={() => copyToClipboard(createdKey)}
                      className="shrink-0"
                    >
                      <Copy className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
                <div className="flex items-start gap-2 p-3 rounded-md bg-yellow-50 border border-yellow-200 text-yellow-700 text-sm">
                   <ShieldAlert className="h-4 w-4 shrink-0 mt-0.5" />
                   <p>Please save this key now. For security reasons, it will not be displayed again.</p>
                </div>
                <DialogFooter>
                  <Button variant="dark" onClick={handleCloseDialog} className="w-full">
                    I have saved my key
                  </Button>
                </DialogFooter>
              </div>
            )}
          </DialogContent>
        </Dialog>
      </div>

      <Card>
        <CardHeader className="pb-4">
          <CardTitle className="text-lg">Active Keys</CardTitle>
          <CardDescription>
            Manage access and revoke keys that are no longer needed.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
               {[1, 2, 3].map(i => (
                  <div key={i} className="h-20 w-full bg-muted/40 animate-pulse rounded-lg"></div>
               ))}
            </div>
          ) : apiKeys.length === 0 ? (
            <div className="text-center py-12 border-2 border-dashed rounded-lg bg-muted/5">
              <div className="bg-muted p-3 rounded-full inline-flex mb-4">
                 <Key className="h-6 w-6 text-muted-foreground" />
              </div>
              <h3 className="text-lg font-medium">No API keys found</h3>
              <p className="text-sm text-muted-foreground max-w-sm mx-auto mt-1 mb-6">
                You haven&apos;t created any API keys yet. Create one to start making requests to the API.
              </p>
              <Button onClick={() => setIsCreateDialogOpen(true)} variant="outline">
                Create your first key
              </Button>
            </div>
          ) : (
            <div>
              <div className="hidden sm:grid grid-cols-[1fr_100px_80px_48px] gap-4 px-4 pb-3 text-xs font-medium text-muted-foreground uppercase tracking-wider border-b">
                <span>Key</span>
                <span className="text-right">Credits Used</span>
                <span className="text-center">Enabled</span>
                <span className="text-center">Delete</span>
              </div>
              <div className="divide-y">
                {apiKeys.map((key) => (
                  <div
                    key={key.id}
                    className="grid grid-cols-1 sm:grid-cols-[1fr_100px_80px_48px] gap-4 items-center px-4 py-4 transition-colors hover:bg-muted/30"
                    style={{ opacity: key.disabled ? 0.5 : 1 }}
                  >
                    <div className="min-w-0">
                      <h3 className="font-bold flex items-center gap-2 truncate">
                        {key.name}
                        {key.disabled && (
                          <Badge variant="destructive" className="h-5 text-[10px] px-1.5 shrink-0">Disabled</Badge>
                        )}
                      </h3>
                      <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-muted-foreground mt-0.5">
                        <span>Created: {new Date(key.createdAt).toLocaleDateString()}</span>
                        {key.lastUsed ? (
                          <span className="flex items-center gap-1 text-orange-500/80">
                            <Activity className="h-3 w-3" /> Last used {new Date(key.lastUsed).toLocaleDateString()}
                          </span>
                        ) : (
                          <span className="text-muted-foreground/60">Never used</span>
                        )}
                      </div>
                    </div>

                    <div className="text-right font-mono font-semibold text-sm tabular-nums">
                      {key.creditsConsumed || 0}
                    </div>

                    <div className="flex justify-center">
                      <Switch
                        checked={!key.disabled}
                        onCheckedChange={() => handleToggleDisabled(key.id, key.disabled)}
                      />
                    </div>

                    <div className="flex justify-center">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => setDeleteTarget(key)}
                        className="text-destructive hover:text-destructive hover:bg-destructive/10 h-8 w-8"
                        title="Delete key"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <Dialog open={!!deleteTarget} onOpenChange={(open) => { if (!open) setDeleteTarget(null); }}>
        <DialogContent className="sm:max-w-sm">
          <DialogHeader>
            <DialogTitle>Delete API Key</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete <span className="font-semibold text-foreground">{deleteTarget?.name}</span>? This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="gap-2 sm:gap-0">
            <Button variant="outline" onClick={() => setDeleteTarget(null)} disabled={isDeleting}>
              Cancel
            </Button>
            <Button variant="dark" onClick={handleDeleteConfirm} disabled={isDeleting}>
              {isDeleting ? <Activity className="mr-2 h-4 w-4 animate-spin" /> : <Trash2 className="mr-2 h-4 w-4" />}
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
