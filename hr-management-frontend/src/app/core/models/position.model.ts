export interface Position {
  id: number;
  code: string;
  name: string;
  description: string;
  status: string;
}

export interface PositionRequest {
  code: string;
  name: string;
  description?: string;
  status?: string;
}

export const POS_STATUS_MAP: Record<string, string> = {
  ACTIVE: 'Đang hoạt động',
  INACTIVE: 'Ngừng hoạt động'
};
