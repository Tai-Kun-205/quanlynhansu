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
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { LeaveRequestService } from '../../core/services/leave-request.service';
import { LeaveRequest, LEAVE_STATUS_MAP } from '../../core/models/leave-request.model';
import { ExportService } from '../../core/services/export.service';

@Component({
  selector: 'app-leave-request-list',
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
    MatTooltipModule,
    MatCardModule,
  ],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group">
        <span class="page-eyebrow">VẬN HÀNH</span>
        <h1>Quản lý đơn nghỉ phép</h1>
        <p>Kiểm tra, phê duyệt và theo dõi các đề nghị nghỉ phép.</p>
      </div>
      <div class="page-header-actions"><button mat-stroked-button (click)="exportExcel()" matTooltip="Xuất danh sách nghỉ phép">
        <mat-icon>file_download</mat-icon> Xuất Excel
      </button></div>
    </div>

    <div class="data-toolbar">
      <mat-form-field appearance="outline" class="toolbar-filter">
        <mat-label>Trạng Thái</mat-label>
        <mat-select [(ngModel)]="filterStatus" (ngModelChange)="search()">
          <mat-option value="">Tất cả</mat-option>
          <mat-option value="PENDING">Chờ Duyệt</mat-option>
          <mat-option value="APPROVED">Đã Duyệt</mat-option>
          <mat-option value="REJECTED">Từ Chối</mat-option>
          <mat-option value="CANCELLED">Đã Hủy</mat-option>
        </mat-select>
      </mat-form-field>
      <span class="table-summary"><mat-icon>event_available</mat-icon>{{ total }} đơn nghỉ phép</span>
    </div>

    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="requests">
            <ng-container matColumnDef="employee">
              <th mat-header-cell *matHeaderCellDef>Nhân Viên</th>
              <td mat-cell *matCellDef="let r">
                <div class="table-person"><span class="table-avatar">{{ (r.employeeName || '?').charAt(0).toUpperCase() }}</span><span><div class="table-primary">{{ r.employeeName }}</div><div class="table-secondary">{{ r.employeeCode }}</div></span></div>
              </td>
            </ng-container>
            <ng-container matColumnDef="startDate">
              <th mat-header-cell *matHeaderCellDef style="width:110px">Từ Ngày</th>
              <td mat-cell *matCellDef="let r">{{ r.startDate }}</td>
            </ng-container>
            <ng-container matColumnDef="endDate">
              <th mat-header-cell *matHeaderCellDef style="width:110px">Đến Ngày</th>
              <td mat-cell *matCellDef="let r">{{ r.endDate }}</td>
            </ng-container>
            <ng-container matColumnDef="reason">
              <th mat-header-cell *matHeaderCellDef>Lý Do</th>
              <td
                mat-cell
                *matCellDef="let r"
                style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap"
              >
                {{ r.reason }}
              </td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Trạng Thái</th>
              <td mat-cell *matCellDef="let r">
                <span [class]="getLeaveBadge(r.status)">{{ statusMap[r.status] || r.status }}</span>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Thao Tác</th>
              <td mat-cell *matCellDef="let r">
                @if (r.status === 'PENDING') {
                  <div class="table-actions">
                    <button
                      mat-icon-button
                      style="color:#16a34a"
                      (click)="approve(r)"
                      matTooltip="Duyệt"
                    >
                      <mat-icon>check_circle</mat-icon>
                    </button>
                    <button mat-icon-button color="warn" (click)="reject(r)" matTooltip="Từ chối">
                      <mat-icon>cancel</mat-icon>
                    </button>
                  </div>
                }

              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="cols"></tr>
            <tr mat-row *matRowDef="let row; columns: cols"></tr>
          </table>
        </div>
        @if (!requests.length) {
          <div class="table-empty">
            <mat-icon>event_note</mat-icon>
            Không có đơn nghỉ phép nào
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
export class LeaveRequestListComponent implements OnInit {
  requests: LeaveRequest[] = [];
  cols = ['employee', 'startDate', 'endDate', 'reason', 'status', 'actions'];
  loading = false;
  filterStatus = '';
  page = 0;
  pageSize = 10;
  total = 0;
  statusMap = LEAVE_STATUS_MAP;

  constructor(
    private svc: LeaveRequestService,
    private exportSvc: ExportService,
    private snack: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.cdr.markForCheck();
    this.svc
      .getAll({ status: this.filterStatus || undefined, page: this.page, size: this.pageSize })
      .subscribe({
        next: (r) => {
          this.requests = r.data.content;
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
    this.exportSvc.leaveRequests({ status: this.filterStatus }).subscribe({
      next: file => this.download(file, 'danh-sach-nghi-phep.xlsx'),
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

  approve(r: LeaveRequest) {
    if (!confirm(`Duyệt đơn nghỉ phép của ${r.employeeName}?`)) return;
    this.svc.approve(r.id).subscribe({
      next: () => {
        this.snack.open('Đã duyệt đơn', 'Đóng', { duration: 3000 });
        this.load();
      },
      error: (e) => {
        this.snack.open(e.error?.message || 'Có lỗi', 'Đóng', { duration: 4000 });
        this.cdr.detectChanges();
      },
    });
  }

  reject(r: LeaveRequest) {
    if (!confirm(`Từ chối đơn nghỉ phép của ${r.employeeName}?`)) return;
    this.svc.reject(r.id).subscribe({
      next: () => {
        this.snack.open('Đã từ chối đơn', 'Đóng', { duration: 3000 });
        this.load();
      },
      error: (e) => {
        this.snack.open(e.error?.message || 'Có lỗi', 'Đóng', { duration: 4000 });
        this.cdr.detectChanges();
      },
    });
  }

  getLeaveBadge(status: string) {
    const m: Record<string, string> = {
      PENDING: 'badge badge-pending',
      APPROVED: 'badge badge-approved',
      REJECTED: 'badge badge-rejected',
      CANCELLED: 'badge badge-cancelled',
    };
    return m[status] || 'badge';
  }
}
