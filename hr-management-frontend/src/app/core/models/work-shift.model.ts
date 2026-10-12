import { PageResponse } from './api-response.model';

export interface WorkShift {
  id: number; code: string; name: string; startTime: string; endTime: string;
  description?: string; active: boolean;
}
export interface WorkAssignment {
  id: number; employeeId: number; employeeCode: string; employeeName: string;
  shiftId: number; shiftCode: string; shiftName: string; startTime: string;
  endTime: string; workDate: string; note?: string;
}
export type WorkAssignmentPage = PageResponse<WorkAssignment>;
