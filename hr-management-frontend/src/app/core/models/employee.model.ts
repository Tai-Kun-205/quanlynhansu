export interface Employee {
  id: number;
  code: string;
  fullName: string;
  dateOfBirth: string;
  gender: string;
  phone: string;
  email: string;
  address: string;
  avatarUrl?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelationship?: string;
  educationLevel?: string;
  major?: string;
  startDate: string;
  seniorityYears?: number;
  departmentId: number;
  departmentName: string;
  positionId: number;
  positionName: string;
  status: string;
  userId: number;
  username: string;
}

export interface EmployeeRequest {
  code: string;
  fullName: string;
  dateOfBirth?: string;
  gender?: string;
  phone?: string;
  email?: string;
  address?: string;
  avatarUrl?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelationship?: string;
  educationLevel?: string;
  major?: string;
  startDate: string;
  departmentId: number;
  positionId: number;
  status?: string;
  username?: string;
  password?: string;
}

export const GENDER_MAP: Record<string, string> = {
  MALE: 'Nam',
  FEMALE: 'Nữ',
  OTHER: 'Khác'
};

export const EMPLOYEE_STATUS_MAP: Record<string, string> = {
  WORKING: 'Đang làm việc',
  ON_LEAVE: 'Tạm nghỉ',
  RESIGNED: 'Nghỉ việc'
};
