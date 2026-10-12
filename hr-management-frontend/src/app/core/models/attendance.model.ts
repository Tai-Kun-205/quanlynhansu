export interface Attendance {
  id: number;
  employeeId: number;
  employeeName: string;
  employeeCode: string;
  workDate: string;
  checkInTime: string;
  checkOutTime: string;
  workHours: number;
  status: string;
}

export const ATTENDANCE_STATUS_MAP: Record<string, string> = {
  PRESENT: 'Có mặt',
  LATE: 'Đi muộn',
  EARLY_LEAVE: 'Về sớm',
  ABSENT: 'Nghỉ',
  NOT_CHECKED: 'Chưa chấm công'
};
