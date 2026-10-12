export interface Dashboard {
  totalEmployees: number;
  workingEmployees: number;
  onLeaveEmployees: number;
  resignedEmployees: number;
  totalDepartments: number;
  totalPositions: number;
  pendingLeaveRequests: number;
  todayAttendanceCount: number;
  employeesByDepartment: { name: string; count: number }[];
  employeesByStatus: { label: string; count: number }[];
  todayAttendanceByStatus: { label: string; count: number }[];
  totalPayroll: number;
  payrollCount: number;
  draftPayrollCount: number;
  finalizedPayrollCount: number;
  totalPayrollWorkDays: number;
  averagePayroll: number;
  payrollByDepartment: { label: string; total: number; count: number }[];
}
