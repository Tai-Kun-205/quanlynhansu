import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Employee, EMPLOYEE_STATUS_MAP, GENDER_MAP } from '../../core/models/employee.model';
import { Attendance, ATTENDANCE_STATUS_MAP } from '../../core/models/attendance.model';
import { LeaveRequest, LEAVE_STATUS_MAP } from '../../core/models/leave-request.model';
import { Payroll } from '../../core/models/payroll.model';
import { EmployeeService } from '../../core/services/employee.service';
import { AttendanceService } from '../../core/services/attendance.service';
import { LeaveRequestService } from '../../core/services/leave-request.service';
import { PayrollService } from '../../core/services/payroll.service';
import { ExportService } from '../../core/services/export.service';
import { of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [CommonModule, FormsModule, MatTabsModule, MatButtonModule, MatIconModule,
    MatProgressSpinnerModule, MatSnackBarModule],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group"><span class="page-eyebrow">PHÂN TÍCH DỮ LIỆU</span>
        <h1>Báo cáo</h1><p>Phân tích chi tiết dữ liệu quản lý nhân sự.</p></div>
    </div>
    <div class="report-filter-bar">
      <label>Tháng <select [(ngModel)]="month" (change)="load()"><option *ngFor="let m of months" [value]="m">{{m}}</option></select></label>
      <label>Năm <input type="number" [(ngModel)]="year" (change)="load()"></label>
      <button mat-raised-button color="primary" [disabled]="exporting" (click)="exportCurrent()"><mat-icon>download</mat-icon>{{ exporting ? 'Đang xuất...' : 'Xuất Excel' }}</button>
    </div>
    @if (loading) { <div class="dashboard-loading"><mat-spinner diameter="36"></mat-spinner><span>Đang tải báo cáo...</span></div> }
    @else {
      <mat-tab-group [(selectedIndex)]="tab">
        <mat-tab label="Báo cáo nhân viên">
          <div class="report-kpis"><div><strong>{{employees.length}}</strong><span>Tổng nhân viên</span></div>
            <div><strong>{{countEmployee('WORKING')}}</strong><span>Đang làm việc</span></div>
            <div><strong>{{countEmployee('ON_LEAVE')}}</strong><span>Tạm nghỉ</span></div>
            <div><strong>{{countEmployee('RESIGNED')}}</strong><span>Nghỉ việc</span></div></div>
          <div class="report-table-wrap"><table><thead><tr><th>Mã nhân viên</th><th>Họ tên</th><th>Phòng ban</th><th>Chức vụ</th><th>Ngày vào làm</th><th>Trạng thái</th></tr></thead>
          <tbody><tr *ngFor="let e of employees"><td>{{e.code}}</td><td>{{e.fullName}}</td><td>{{e.departmentName}}</td><td>{{e.positionName}}</td><td>{{e.startDate | date:'dd/MM/yyyy'}}</td><td>{{employeeStatus(e.status)}}</td></tr></tbody></table></div>
        </mat-tab>
        <mat-tab label="Báo cáo chấm công">
          <div class="report-kpis"><div><strong>{{attendance.length}}</strong><span>Bản ghi</span></div>
            <div><strong>{{countAttendance('PRESENT')}}</strong><span>Có mặt</span></div><div><strong>{{countAttendance('LATE')}}</strong><span>Đi muộn</span></div>
            <div><strong>{{countAttendance('EARLY_LEAVE')}}</strong><span>Về sớm</span></div></div>
          <div class="report-table-wrap"><table><thead><tr><th>Mã nhân viên</th><th>Họ tên</th><th>Ngày</th><th>Giờ vào</th><th>Giờ ra</th><th>Trạng thái</th></tr></thead>
          <tbody><tr *ngFor="let a of attendance"><td>{{a.employeeCode}}</td><td>{{a.employeeName}}</td><td>{{a.workDate | date:'dd/MM/yyyy'}}</td><td>{{a.checkInTime || '—'}}</td><td>{{a.checkOutTime || '—'}}</td><td>{{attendanceStatus(a.status)}}</td></tr></tbody></table></div>
        </mat-tab>
        <mat-tab label="Báo cáo nghỉ phép">
          <div class="report-kpis"><div><strong>{{leaves.length}}</strong><span>Tổng đơn</span></div>
            <div><strong>{{countLeave('PENDING')}}</strong><span>Chờ duyệt</span></div><div><strong>{{countLeave('APPROVED')}}</strong><span>Đã duyệt</span></div><div><strong>{{countLeave('REJECTED')}}</strong><span>Từ chối</span></div></div>
          <div class="report-table-wrap"><table><thead><tr><th>Mã nhân viên</th><th>Họ tên</th><th>Từ ngày</th><th>Đến ngày</th><th>Lý do</th><th>Trạng thái</th></tr></thead>
          <tbody><tr *ngFor="let l of leaves"><td>{{l.employeeCode}}</td><td>{{l.employeeName}}</td><td>{{l.startDate | date:'dd/MM/yyyy'}}</td><td>{{l.endDate | date:'dd/MM/yyyy'}}</td><td>{{l.reason}}</td><td>{{leaveStatus(l.status)}}</td></tr></tbody></table></div>
        </mat-tab>
        <mat-tab label="Báo cáo bảng lương">
          <div class="report-kpis"><div><strong>{{payrolls.length}}</strong><span>Số bảng lương</span></div>
            <div><strong>{{totalPayroll | number:'1.0-0'}} ₫</strong><span>Tổng quỹ lương</span></div>
            <div><strong>{{countPayroll('DRAFT')}}</strong><span>Bảng nháp</span></div><div><strong>{{countPayroll('FINALIZED')}}</strong><span>Đã chốt</span></div></div>
          <div class="report-table-wrap"><table><thead><tr><th>Mã nhân viên</th><th>Họ tên</th><th>Phòng ban</th><th>Ngày công</th><th>Lương theo công</th><th>Thực nhận</th><th>Trạng thái</th></tr></thead>
          <tbody><tr *ngFor="let p of payrolls"><td>{{p.employeeCode}}</td><td>{{p.employeeName}}</td><td>{{p.departmentName}}</td><td>{{p.actualWorkDays}} / {{p.standardWorkDays}}</td><td>{{p.attendanceSalary | number:'1.0-0'}} ₫</td><td>{{p.netSalary | number:'1.0-0'}} ₫</td><td>{{p.status === 'FINALIZED' ? 'Đã chốt' : 'Bản nháp'}}</td></tr></tbody></table></div>
        </mat-tab>
      </mat-tab-group>
    }
  `
})
export class ReportComponent implements OnInit {
  tab = 0; loading = true; month = new Date().getMonth() + 1; year = new Date().getFullYear();
  exporting = false;
  months = Array.from({length: 12}, (_, i) => i + 1);
  employees: Employee[] = []; attendance: Attendance[] = []; leaves: LeaveRequest[] = []; payrolls: Payroll[] = [];
  constructor(private employeeService: EmployeeService, private attendanceService: AttendanceService,
    private leaveService: LeaveRequestService, private payrollService: PayrollService,
    private exportService: ExportService, private snack: MatSnackBar) {}
  ngOnInit() { this.load(); }
  load() {
    this.loading = true;
    const emptyPage = () => of({ data: { content: [], totalElements: 0, totalPages: 0, size: 1000, number: 0 } } as any);
    let pending = 4;
    const complete = () => { pending -= 1; if (pending === 0) this.loading = false; };
    this.employeeService.getAll({ page: 0, size: 1000 }).pipe(catchError(() => emptyPage()), finalize(complete))
      .subscribe({ next: r => this.employees = r.data.content });
    this.attendanceService.getAll({ page: 0, size: 1000, startDate: this.periodStart(), endDate: this.periodEnd() })
      .pipe(catchError(() => emptyPage()), finalize(complete))
      .subscribe({ next: r => this.attendance = r.data.content });
    this.leaveService.getAll({ page: 0, size: 1000 }).pipe(catchError(() => emptyPage()))
      .pipe(finalize(complete))
      .subscribe({ next: r => this.leaves = r.data.content.filter((l: LeaveRequest) => this.overlapsPeriod(l.startDate, l.endDate)) });
    this.payrollService.search({ page: 0, size: 1000, month: this.month, year: this.year })
      .pipe(catchError(() => emptyPage()), finalize(complete))
      .subscribe({ next: r => this.payrolls = r.data.content });
    this.loading = false;
  }
  countEmployee(s: string) { return this.employees.filter(e => e.status === s).length; }
  countAttendance(s: string) { return this.attendance.filter(a => a.status === s).length; }
  countLeave(s: string) { return this.leaves.filter(l => l.status === s).length; }
  countPayroll(s: string) { return this.payrolls.filter(p => p.status === s).length; }
  get totalPayroll() { return this.payrolls.reduce((sum, p) => sum + (p.netSalary || 0), 0); }
  employeeStatus(s: string) { return EMPLOYEE_STATUS_MAP[s] || 'Không xác định'; }
  attendanceStatus(s: string) { return ATTENDANCE_STATUS_MAP[s] || 'Không xác định'; }
  leaveStatus(s: string) { return LEAVE_STATUS_MAP[s] || 'Không xác định'; }
  exportCurrent() {
    if (this.tab === 0) this.exportEmployeeReport();
    else if (this.tab === 1) this.exportAttendanceReport();
    else if (this.tab === 2) this.exportLeaveReport();
    else this.exportSalaryReport();
  }
  exportEmployeeReport() {
    this.download(this.exportService.employees(), 'danh-sach-nhan-vien.xlsx');
  }
  exportAttendanceReport() {
    this.download(this.exportService.attendances({ startDate: this.periodStart(), endDate: this.periodEnd() }), `cham-cong-thang-${this.month}-${this.year}.xlsx`);
  }
  exportLeaveReport() {
    this.download(this.exportService.leaveRequests({ month: this.month, year: this.year }), `nghi-phep-thang-${this.month}-${this.year}.xlsx`);
  }
  exportSalaryReport() {
    this.download(this.exportService.payrolls({ month: this.month, year: this.year }), `bang-luong-thang-${this.month}-${this.year}.xlsx`);
  }
  private periodStart() { return `${this.year}-${String(this.month).padStart(2, '0')}-01`; }
  private periodEnd() {
    const lastDay = new Date(this.year, this.month, 0).getDate();
    return `${this.year}-${String(this.month).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`;
  }
  private overlapsPeriod(start: string, end: string) {
    return end >= this.periodStart() && start <= this.periodEnd();
  }
  private download(request: import('rxjs').Observable<Blob>, filename: string) {
    this.exporting = true;
    request.subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob); const a = document.createElement('a');
        a.href = url; a.download = filename; a.click(); URL.revokeObjectURL(url);
        this.exporting = false;
        this.snack.open('Xuất Excel thành công', 'Đóng', {duration: 2500});
      },
      error: () => { this.exporting = false; this.snack.open('Không thể xuất Excel', 'Đóng', {duration: 3500}); }
    });
  }
}
