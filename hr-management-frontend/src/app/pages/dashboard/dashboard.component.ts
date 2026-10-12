import { Component, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DashboardService } from '../../core/services/dashboard.service';
import { Dashboard } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="page-header">
      <div class="page-title-group">
        <span class="page-eyebrow">TỔNG QUAN DOANH NGHIỆP</span>
        <h1>Tổng quan hệ thống</h1>
        <p>Theo dõi nhanh tình hình nhân sự và vận hành trong ngày.</p>
      </div>
    </div>

    @if (loading) {
      <div class="dashboard-loading">
        <mat-spinner diameter="38"></mat-spinner>
        <span>Đang tổng hợp dữ liệu...</span>
      </div>
    } @else if (error) {
      <div class="state-card state-error">
        <mat-icon>cloud_off</mat-icon>
        <div><strong>Không thể tải dữ liệu tổng quan</strong><p>{{ error }}</p></div>
      </div>
    } @else {
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#667eea,#764ba2)">
            <mat-icon>people</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.totalEmployees ?? 0 }}</div>
            <div class="stat-label">Tổng Nhân Viên</div>
            <div class="stat-description">Tất cả nhân sự</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#20a486,#38bfa1)">
            <mat-icon>payments</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number stat-number-money">{{ (data?.totalPayroll ?? 0) | number:'1.0-0' }} ₫</div>
            <div class="stat-label">Tổng quỹ lương</div>
            <div class="stat-description">{{ data?.payrollCount ?? 0 }} bảng lương</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#20a486,#38bfa1)">
            <mat-icon>verified</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.finalizedPayrollCount ?? 0 }}</div>
            <div class="stat-label">Bảng lương đã chốt</div>
            <div class="stat-description">Đang nháp: {{ data?.draftPayrollCount ?? 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#43e97b,#38f9d7)">
            <mat-icon>check_circle</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.workingEmployees ?? 0 }}</div>
            <div class="stat-label">Đang Làm Việc</div>
            <div class="stat-description">Đang hoạt động</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#f093fb,#f5576c)">
            <mat-icon>pause_circle</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.onLeaveEmployees ?? 0 }}</div>
            <div class="stat-label">Tạm Nghỉ</div>
            <div class="stat-description">Đang tạm nghỉ</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#fc4a1a,#f7b733)">
            <mat-icon>person_off</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.resignedEmployees ?? 0 }}</div>
            <div class="stat-label">Đã Nghỉ Việc</div>
            <div class="stat-description">Đã rời công ty</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#4facfe,#00f2fe)">
            <mat-icon>business</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.totalDepartments ?? 0 }}</div>
            <div class="stat-label">Phòng Ban</div>
            <div class="stat-description">Đơn vị tổ chức</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#a18cd1,#fbc2eb)">
            <mat-icon>work</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.totalPositions ?? 0 }}</div>
            <div class="stat-label">Chức Vụ</div>
            <div class="stat-description">Danh mục chức vụ</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#ff9a9e,#fad0c4)">
            <mat-icon>pending_actions</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.pendingLeaveRequests ?? 0 }}</div>
            <div class="stat-label">Đơn Chờ Duyệt</div>
            <div class="stat-description">Cần xử lý hôm nay</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background:linear-gradient(135deg,#0fd850,#f9f047)">
            <mat-icon>how_to_reg</mat-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ data?.todayAttendanceCount ?? 0 }}</div>
            <div class="stat-label">Chấm Công Hôm Nay</div>
            <div class="stat-description">Lượt ghi nhận</div>
          </div>
        </div>
      </div>
      @if (data) {
        <div class="charts-grid">
          <section class="chart-card">
            <div class="chart-heading"><div><span class="chart-kicker">PHÂN BỔ NHÂN SỰ</span><h2>Nhân viên theo phòng ban</h2></div><mat-icon>corporate_fare</mat-icon></div>
            @if (data.employeesByDepartment?.length) {
              <div class="bar-chart">
                @for (item of data.employeesByDepartment; track item.name) {
                  <div class="bar-row">
                    <span class="bar-label" [title]="item.name">{{ item.name }}</span>
                    <div class="bar-track"><div class="bar-fill" [style.width.%]="barWidth(item.count, maxDepartmentCount)"></div></div>
                    <strong>{{ item.count }}</strong>
                  </div>
                }
              </div>
            } @else {
              <div class="chart-empty">Chưa có dữ liệu phòng ban</div>
            }
          </section>
          <section class="chart-card">
            <div class="chart-heading"><div><span class="chart-kicker">ĐÃI NGỘ</span><h2>Quỹ lương theo phòng ban</h2></div><mat-icon>account_balance</mat-icon></div>
            @if (data.payrollByDepartment?.length) {
              @for (item of data.payrollByDepartment; track item.label) {
                <div class="bar-row payroll-bar-row">
                  <span class="bar-label" [title]="item.label">{{ item.label }}</span>
                  <div class="bar-track"><div class="bar-fill" [style.width.%]="barWidth(item.total, maxPayrollTotal)"></div></div>
                  <strong>{{ item.total | number:'1.0-0' }} ₫</strong>
                </div>
              }
            } @else {
              <div class="chart-empty">Chưa có dữ liệu bảng lương</div>
            }
          </section>
          <section class="chart-card">
            <div class="chart-heading"><div><span class="chart-kicker">TÌNH TRẠNG NHÂN SỰ</span><h2>Nhân viên theo trạng thái</h2></div><mat-icon>donut_large</mat-icon></div>
            @if (data.employeesByStatus?.length) {
              <div class="status-chart">
                @for (item of data.employeesByStatus; track item.label) {
                  <div class="status-row">
                    <span>{{ item.label }}</span><strong>{{ item.count }}</strong>
                    <div class="status-track"><div class="status-fill" [style.width.%]="barWidth(item.count, maxEmployeeStatusCount)"></div></div>
                  </div>
                }
              </div>
            } @else {
              <div class="chart-empty">Chưa có dữ liệu trạng thái</div>
            }
          </section>
          <section class="chart-card chart-card-wide">
            <div class="chart-heading"><div><span class="chart-kicker">VẬN HÀNH HÔM NAY</span><h2>Thống kê chấm công hôm nay</h2></div><mat-icon>schedule</mat-icon></div>
            @if (data.todayAttendanceByStatus?.length) {
              <div class="attendance-chart">
                @for (item of data.todayAttendanceByStatus; track item.label) {
                  <div class="attendance-item">
                    <div class="attendance-value">{{ item.count }}</div>
                    <div class="attendance-bar"><div class="attendance-fill" [style.height.%]="barWidth(item.count, maxAttendanceCount)"></div></div>
                    <div class="attendance-label">{{ item.label }}</div>
                  </div>
                }
              </div>
            } @else {
              <div class="chart-empty">Chưa có dữ liệu chấm công</div>
            }
          </section>
        </div>
      }
    }
  `
})
export class DashboardComponent implements OnInit {
  data: Dashboard | null = null;
  loading = true;
  error = '';
  maxDepartmentCount = 0;
  maxEmployeeStatusCount = 0;
  maxAttendanceCount = 0;
  maxPayrollTotal = 0;

  constructor(private dashboardService: DashboardService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.dashboardService.getSummary().subscribe({
      next: r => {
        this.data = r.data;
        this.maxDepartmentCount = this.max(r.data.employeesByDepartment);
        this.maxEmployeeStatusCount = this.max(r.data.employeesByStatus);
        this.maxAttendanceCount = this.max(r.data.todayAttendanceByStatus);
        this.maxPayrollTotal = this.maxValue(r.data.payrollByDepartment);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: e => {
        this.loading = false;
        this.error = 'Vui lòng kiểm tra kết nối và thử tải lại trang.';
        this.cdr.detectChanges();
        console.error('Không thể tải dữ liệu tổng quan:', e);
      }
    });
  }

  barWidth(value: number, max: number): number {
    return max > 0 ? Math.max(4, (value / max) * 100) : 0;
  }

  private max(items: { count: number }[] | undefined): number {
    return items?.reduce((current, item) => Math.max(current, item.count), 0) || 0;
  }

  private maxValue(items: { total: number }[] | undefined): number {
    return items?.reduce((current, item) => Math.max(current, item.total), 0) || 0;
  }
}
