export interface LeaveRequest {
  id: number;
  employeeId: number;
  employeeName: string;
  employeeCode: string;
  startDate: string;
  endDate: string;
  reason: string;
  createdAt: string;
  status: string;
}

export interface LeaveRequestCreate {
  startDate: string;
  endDate: string;
  reason: string;
}

export const LEAVE_STATUS_MAP: Record<string, string> = {
  PENDING: 'Chờ duyệt',
  APPROVED: 'Đã duyệt',
  REJECTED: 'Từ chối',
  CANCELLED: 'Đã hủy'
};
