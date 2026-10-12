export interface ActivityLog {
  id: number; actorUsername: string; action: string; module: string;
  targetType?: string; targetId?: number; description?: string;
  createdAt: string; ipAddress?: string;
}
