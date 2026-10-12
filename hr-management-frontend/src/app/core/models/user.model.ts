export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  status: string;
  employeeId: number;
  employeeName: string;
}

export const ROLE_MAP: Record<string, string> = {
  ADMIN: 'Quản trị viên',
  EMPLOYEE: 'Nhân viên'
};

export const ACCOUNT_STATUS_MAP: Record<string, string> = {
  ACTIVE: 'Đang hoạt động',
  LOCKED: 'Đã khóa'
};
