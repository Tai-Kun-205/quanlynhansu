import { Component, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AttendanceService } from '../../core/services/attendance.service';
import { Attendance, ATTENDANCE_STATUS_MAP } from '../../core/models/attendance.model';
import { ExportService } from '../../core/services/export.service';
import { NetworkAccessService } from '../../core/services/network-access.service';
import { NetworkStatus } from '../../core/models/network-status.model';

@Component({
  selector: 'app-attendance-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatTooltipModule,
  ],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group"><span class="page-eyebrow">VẬN HÀNH</span><h1>Quản lý chấm công</h1><p>Theo dõi thời gian làm việc và tình trạng chấm công của nhân viên.</p></div>
      <div class="page-header-actions"><button mat-stroked-button (click)="exportExcel()" matTooltip="Xuất danh sách chấm công"><mat-icon>file_download</mat-icon> Xuất Excel</button></div>
    </div>

    <section class="network-demo-card">
      <div class="network-demo-content">
        <div class="network-demo-icon" [class.offline]="networkStatus && !networkStatus.companyNetwork">
          <mat-icon>{{ networkStatus?.companyNetwork ? 'wifi' : 'wifi_off' }}</mat-icon>
        </div>
        <div>
          <span class="page-eyebrow">MÔ PHỎNG DEMO LOCALHOST</span>
          <h2>Mạng công ty</h2>
          @if (networkLoading) {
            <p>Đang tải trạng thái kết nối...</p>
          } @else if (networkStatus) {
            <p [class.network-offline-text]="!networkStatus.companyNetwork">
              {{ networkStatus.companyNetwork ? 'Đang kết nối mạng công ty' : 'Ngoài mạng công ty' }}
            </p>
          } @else {
            <p>Không thể tải trạng thái mạng mô phỏng.</p>
          }
        </div>
      </div>
      <button mat-stroked-button (click)="toggleNetwork()" [disabled]="networkLoading || !networkStatus">
        <mat-icon>{{ networkStatus?.companyNetwork ? 'wifi_off' : 'wifi' }}</mat-icon>
        {{ networkStatus?.companyNetwork ? 'Chuyển sang ngoài mạng công ty' : 'Chuyển sang mạng công ty' }}
      </button>
    </section>

    <div class="data-toolbar">
      <mat-form-field appearance="outline" class="toolbar-filter">
        <mat-label>Từ ngày</mat-label>
        <input matInput [(ngModel)]="startDate" type="date" (change)="search()" />
      </mat-form-field>
      <mat-form-field appearance="outline" class="toolbar-filter">
        <mat-label>Đến ngày</mat-label>
        <input matInput [(ngModel)]="endDate" type="date" (change)="search()" />
      </mat-form-field>
      <mat-form-field appearance="outline" class="toolbar-filter">
        <mat-label>Trạng Thái</mat-label>
        <mat-select [(ngModel)]="filterStatus" (ngModelChange)="search()">
          <mat-option value="">Tất cả</mat-option>
          <mat-option value="PRESENT">Có Mặt</mat-option>
          <mat-option value="LATE">Đi Muộn</mat-option>
          <mat-option value="EARLY_LEAVE">Về Sớm</mat-option>
          <mat-option value="ABSENT">Nghỉ</mat-option>
        </mat-select>
      </mat-form-field>
      <span class="table-summary"><mat-icon>schedule</mat-icon>{{ total }} bản ghi</span>
    </div>

    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="records">
            <ng-container matColumnDef="employeeCode">
              <th mat-header-cell *matHeaderCellDef style="width:100px">Mã NV</th>
              <td mat-cell *matCellDef="let a"><span class="table-code">{{ a.employeeCode }}</span></td>
            </ng-container>
            <ng-container matColumnDef="employeeName">
              <th mat-header-cell *matHeaderCellDef>Họ và Tên</th>
              <td mat-cell *matCellDef="let a"><div class="table-person"><span class="table-avatar">{{ (a.employeeName || '?').charAt(0).toUpperCase() }}</span><span class="table-primary">{{ a.employeeName }}</span></div></td>
            </ng-container>
            <ng-container matColumnDef="workDate">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Ngày Làm</th>
              <td mat-cell *matCellDef="let a">{{ a.workDate }}</td>
            </ng-container>
            <ng-container matColumnDef="checkInTime">
              <th mat-header-cell *matHeaderCellDef style="width:110px">Giờ Vào</th>
              <td mat-cell *matCellDef="let a">
                <span class="table-time">{{ a.checkInTime ? formatTime(a.checkInTime) : '—' }}</span>
              </td>
            </ng-container>
            <ng-container matColumnDef="checkOutTime">
              <th mat-header-cell *matHeaderCellDef style="width:110px">Giờ Ra</th>
              <td mat-cell *matCellDef="let a">
                <span class="table-time">{{ a.checkOutTime ? formatTime(a.checkOutTime) : '—' }}</span>
              </td>
            </ng-container>
            <ng-container matColumnDef="workHours">
              <th mat-header-cell *matHeaderCellDef style="width:100px;text-align:center">
                Số Giờ
              </th>
              <td mat-cell *matCellDef="let a" style="text-align:center">
                {{ a.workHours ?? '—' }}
              </td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef style="width:130px">Trạng Thái</th>
              <td mat-cell *matCellDef="let a">
                <span [class]="getAttBadge(a.status)">{{ statusMap[a.status] || a.status }}</span>
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="cols"></tr>
            <tr mat-row *matRowDef="let row; columns: cols"></tr>
          </table>
        </div>
        @if (!records.length) {
          <div class="table-empty">
            <mat-icon>fingerprint</mat-icon>
            Không có dữ liệu chấm công
          </div>
        }

        <mat-paginator
          [length]="total"
          [pageSize]="pageSize"
          [pageSizeOptions]="[10, 20, 50]"
          (page)="onPage($event)"
        ></mat-paginator>
      }
    </div>
  `,
})
export class AttendanceListComponent implements OnInit {
  records: Attendance[] = [];
  cols = [
    'employeeCode',
    'employeeName',
    'workDate',
    'checkInTime',
    'checkOutTime',
    'workHours',
    'status',
  ];
  loading = false;
  startDate = '';
  endDate = '';
  filterStatus = '';
  page = 0;
  pageSize = 10;
  total = 0;
  statusMap = ATTENDANCE_STATUS_MAP;

  constructor(
    private svc: AttendanceService,
    private exportSvc: ExportService,
    private snack: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private networkService: NetworkAccessService,
  ) {}

  ngOnInit() {
    this.loadNetworkStatus();
    this.load();
  }

  networkStatus: NetworkStatus | null = null;
  networkLoading = false;

  loadNetworkStatus() {
    this.networkLoading = true;
    this.networkService.getStatus().subscribe({
      next: response => {
        this.networkStatus = response.data;
        this.networkLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.networkLoading = false;
        this.cdr.markForCheck();
        this.snack.open('Không thể tải trạng thái mạng mô phỏng', 'Đóng', { duration: 4000 });
      }
    });
  }

  toggleNetwork() {
    if (!this.networkStatus) return;
    this.networkLoading = true;
    this.networkService.updateStatus(!this.networkStatus.companyNetwork).subscribe({
      next: response => {
        this.networkStatus = response.data;
        this.networkLoading = false;
        this.cdr.markForCheck();
        this.snack.open(response.message, 'Đóng', { duration: 3000 });
      },
      error: error => {
        this.networkLoading = false;
        this.cdr.detectChanges();
        this.snack.open(error.error?.message || 'Không thể cập nhật trạng thái mạng', 'Đóng', { duration: 4000 });
      }
    });
  }

  load() {
    this.loading = true;
    this.cdr.markForCheck();
    this.svc
      .getAll({
        startDate: this.startDate || undefined,
        endDate: this.endDate || undefined,
        status: this.filterStatus || undefined,
        page: this.page,
        size: this.pageSize,
      })
      .subscribe({
        next: (r) => {
          this.records = r.data.content;
          this.total = r.data.totalElements;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false;
          this.cdr.detectChanges();
        },
      });
  }

  search() {
    this.page = 0;
    this.load();
  }
  onPage(e: PageEvent) {
    this.page = e.pageIndex;
    this.pageSize = e.pageSize;
    this.load();
  }

  exportExcel() {
    this.exportSvc.attendances({ startDate: this.startDate, endDate: this.endDate, status: this.filterStatus }).subscribe({
      next: file => this.download(file, 'danh-sach-cham-cong.xlsx'),
      error: () => this.snack.open('Không thể xuất file', 'Đóng', { duration: 4000 })
    });
  }

  private download(file: Blob, filename: string) {
    const url = URL.createObjectURL(file);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
    this.snack.open('Xuất file thành công', 'Đóng', { duration: 3000 });
  }

  formatTime(t: string) {
    return t?.substring(0, 5) || '';
  }

  getAttBadge(status: string) {
    const m: Record<string, string> = {
      PRESENT: 'badge badge-present',
      LATE: 'badge badge-late',
      EARLY_LEAVE: 'badge badge-early_leave',
      ABSENT: 'badge badge-absent',
    };
    return m[status] || 'badge';
  }
}
