export interface Department {
  id: number;
  code: string;
  name: string;
  description: string;
  status: string;
  employeeCount: number;
}

export interface DepartmentRequest {
  code: string;
  name: string;
  description?: string;
  status?: string;
}

export const DEPT_STATUS_MAP: Record<string, string> = {
  ACTIVE: 'Đang hoạt động',
  INACTIVE: 'Ngừng hoạt động'
};
