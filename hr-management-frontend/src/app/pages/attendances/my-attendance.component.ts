import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef,
  ChangeDetectionStrategy,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { AttendanceService } from '../../core/services/attendance.service';
import { Attendance, ATTENDANCE_STATUS_MAP } from '../../core/models/attendance.model';
import { NetworkAccessService } from '../../core/services/network-access.service';
import { NetworkStatus } from '../../core/models/network-status.model';

@Component({
  selector: 'app-my-attendance',
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
    MatSnackBarModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatCardModule,
  ],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group"><span class="page-eyebrow">CÁ NHÂN</span><h1>Chấm công</h1><p>Ghi nhận giờ làm việc và theo dõi lịch sử chấm công của bạn.</p></div>
    </div>

    <!-- Khu vực chấm công -->
    <div class="attendance-action-card">
      <div class="employee-network-status" [class.offline]="networkStatus && !networkStatus.companyNetwork">
        <mat-icon>{{ networkStatus?.companyNetwork ? 'wifi' : 'wifi_off' }}</mat-icon>
        <span>
          @if (networkLoading) { Đang kiểm tra trạng thái mạng... }
          @else if (networkStatus?.companyNetwork) { Đang kết nối mạng công ty }
          @else { Không thuộc mạng công ty }
        </span>
      </div>
      <div class="time-display">{{ currentTime }}</div>
      <div class="date-display">{{ currentDate }}</div>
      <div class="checkin-btns">
        <button
          mat-raised-button
          color="primary"
          (click)="checkIn()"
          [disabled]="checkingIn || todayRecord?.checkInTime || networkLoading || networkStatus?.companyNetwork === false"
        >
          @if (checkingIn) {
            <mat-spinner diameter="20" style="display:inline-block;margin-right:8px"></mat-spinner>
          }
          <mat-icon>login</mat-icon> Chấm Công Vào
        </button>
        <button
          mat-raised-button
          color="accent"
          (click)="checkOut()"
          [disabled]="checkingOut || !todayRecord?.checkInTime || todayRecord?.checkOutTime || networkLoading || networkStatus?.companyNetwork === false"
        >
          @if (checkingOut) {
            <mat-spinner diameter="20" style="display:inline-block;margin-right:8px"></mat-spinner>
          }
          <mat-icon>logout</mat-icon> Chấm Công Ra
        </button>
      </div>
      @if (todayRecord) {
        <div
          style="margin-top:20px;padding:16px;background:#f0f4ff;border-radius:10px;text-align:left"
        >
          <div style="font-weight:600;color:#1a237e;margin-bottom:8px">
            Hôm nay ({{ todayRecord.workDate }})
          </div>
          <div style="display:flex;gap:24px;flex-wrap:wrap">
            <div>
              <span style="color:#718096;font-size:12px">Giờ vào: </span>
              <strong>{{
                todayRecord.checkInTime ? formatTime(todayRecord.checkInTime) : 'Chưa chấm'
              }}</strong>
            </div>
            <div>
              <span style="color:#718096;font-size:12px">Giờ ra: </span>
              <strong>{{
                todayRecord.checkOutTime ? formatTime(todayRecord.checkOutTime) : 'Chưa chấm'
              }}</strong>
            </div>
            <div>
              <span style="color:#718096;font-size:12px">Số giờ: </span>
              <strong>{{ todayRecord.workHours ?? '—' }}</strong>
            </div>
            <div>
              <span [class]="getAttBadge(todayRecord.status)">{{
                statusMap[todayRecord.status] || todayRecord.status
              }}</span>
            </div>
          </div>
        </div>
      }
    </div>

    <!-- Lịch sử chấm công -->
    <div class="section-heading">
      <div><span class="page-eyebrow">LỊCH SỬ</span><h2>Lịch sử chấm công</h2></div>
      <span class="table-summary"><mat-icon>history</mat-icon>{{ total }} bản ghi</span>
    </div>
    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="records">
            <ng-container matColumnDef="workDate">
              <th mat-header-cell *matHeaderCellDef>Ngày</th>
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
            Chưa có lịch sử chấm công
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
export class MyAttendanceComponent implements OnInit, OnDestroy {
  records: Attendance[] = [];
  todayRecord: Attendance | null = null;
  cols = ['workDate', 'checkInTime', 'checkOutTime', 'workHours', 'status'];
  loading = false;
  checkingIn = false;
  checkingOut = false;
  page = 0;
  pageSize = 10;
  total = 0;
  currentTime = '';
  currentDate = '';
  private timer: any;
  statusMap = ATTENDANCE_STATUS_MAP;

  constructor(
    private svc: AttendanceService,
    private snack: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private networkService: NetworkAccessService,
  ) {}

  ngOnInit() {
    this.updateClock();
    this.timer = setInterval(() => this.updateClock(), 1000);
    this.load();
    this.loadToday();
    this.loadNetworkStatus();
  }

  networkStatus: NetworkStatus | null = null;
  networkLoading = false;

  loadNetworkStatus() {
    this.networkLoading = true;
    this.networkService.getStatus().subscribe({
      next: response => {
        this.networkStatus = response.data;
        this.networkLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.networkLoading = false;
        this.cdr.detectChanges();
        this.snack.open('Không thể tải trạng thái mạng mô phỏng', 'Đóng', { duration: 4000 });
      }
    });
  }

  ngOnDestroy() {
    clearInterval(this.timer);
  }

  updateClock() {
    const now = new Date();
    this.currentTime = now.toLocaleTimeString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
    this.currentDate = now.toLocaleDateString('vi-VN', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
    this.cdr.detectChanges();
  }

  load() {
    this.loading = true;
    this.cdr.markForCheck();
    this.svc.getMy({ page: this.page, size: this.pageSize }).subscribe({
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

  loadToday() {
    this.svc.getMy({ page: 0, size: 1 }).subscribe({
      next: (r) => {
        const today = new Date().toISOString().split('T')[0];
        this.todayRecord = r.data.content.find((a) => a.workDate === today) || null;
        this.cdr.detectChanges();
      },
    });
  }

  onPage(e: PageEvent) {
    this.page = e.pageIndex;
    this.pageSize = e.pageSize;
    this.load();
  }

  checkIn() {
    this.checkingIn = true;
    this.cdr.markForCheck();
    this.svc.checkIn().subscribe({
      next: (r) => {
        this.checkingIn = false;
        this.snack.open('Chấm công vào thành công!', 'Đóng', { duration: 3000 });
        this.todayRecord = r.data;
        this.load();
      },
      error: (e) => {
        this.checkingIn = false;
        this.cdr.detectChanges();
        this.snack.open(e.error?.message || 'Có lỗi xảy ra', 'Đóng', { duration: 4000 });
      },
    });
  }

  checkOut() {
    this.checkingOut = true;
    this.cdr.markForCheck();
    this.svc.checkOut().subscribe({
      next: (r) => {
        this.checkingOut = false;
        this.snack.open('Chấm công ra thành công!', 'Đóng', { duration: 3000 });
        this.todayRecord = r.data;
        this.load();
      },
      error: (e) => {
        this.checkingOut = false;
        this.cdr.detectChanges();
        this.snack.open(e.error?.message || 'Có lỗi xảy ra', 'Đóng', { duration: 4000 });
      },
    });
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
