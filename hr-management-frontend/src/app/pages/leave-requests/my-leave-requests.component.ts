import { Component, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
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
import { LeaveRequestService } from '../../core/services/leave-request.service';
import { LeaveRequest, LEAVE_STATUS_MAP } from '../../core/models/leave-request.model';

@Component({
  selector: 'app-my-leave-requests',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
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
  ],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group"><span class="page-eyebrow">CÁ NHÂN</span><h1>Đơn nghỉ phép của tôi</h1><p>Gửi yêu cầu và theo dõi trạng thái các đơn nghỉ phép.</p></div>
      <div class="page-header-actions"><button mat-raised-button color="primary" (click)="openForm()"><mat-icon>add</mat-icon> Gửi đơn nghỉ phép</button></div>
    </div>

    <div class="section-heading"><div><span class="page-eyebrow">LỊCH SỬ YÊU CẦU</span><h2>Danh sách đơn nghỉ phép</h2></div><span class="table-summary"><mat-icon>event_available</mat-icon>{{ total }} đơn</span></div>
    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="requests">
            <ng-container matColumnDef="startDate">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Từ Ngày</th>
              <td mat-cell *matCellDef="let r">{{ r.startDate }}</td>
            </ng-container>
            <ng-container matColumnDef="endDate">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Đến Ngày</th>
              <td mat-cell *matCellDef="let r">{{ r.endDate }}</td>
            </ng-container>
            <ng-container matColumnDef="reason">
              <th mat-header-cell *matHeaderCellDef>Lý Do</th>
              <td
                mat-cell
                *matCellDef="let r"
                class="table-muted"
              >
                {{ r.reason }}
              </td>
            </ng-container>
            <ng-container matColumnDef="createdAt">
              <th mat-header-cell *matHeaderCellDef style="width:150px">Ngày Gửi</th>
              <td mat-cell *matCellDef="let r" class="table-date">
                {{ r.createdAt | date: 'dd/MM/yyyy HH:mm' }}
              </td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Trạng Thái</th>
              <td mat-cell *matCellDef="let r">
                <span [class]="getLeaveBadge(r.status)">{{ statusMap[r.status] || r.status }}</span>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef style="width:80px">Hủy</th>
              <td mat-cell *matCellDef="let r">
                @if (r.status === 'PENDING') {
                  <button mat-icon-button color="warn" (click)="cancel(r)" title="Hủy đơn">
                    <mat-icon>cancel</mat-icon>
                  </button>
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
            Bạn chưa có đơn nghỉ phép nào
          </div>
        }
        <mat-paginator
          [length]="total"
          [pageSize]="pageSize"
          [pageSizeOptions]="[10, 20]"
          (page)="onPage($event)"
        ></mat-paginator>
      }
    </div>

    @if (showForm) {
      <div class="dialog-overlay" (click)="closeForm()">
        <mat-card class="dialog-card" (click)="$event.stopPropagation()">
          <mat-card-header>
            <mat-card-title>Gửi Đơn Nghỉ Phép</mat-card-title>
          </mat-card-header>
          <mat-card-content style="padding:20px 16px 0">
            <form [formGroup]="frm">
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Từ Ngày *</mat-label>
                  <input matInput formControlName="startDate" type="date" [min]="minDate" />
                  @if (
                    frm.get('startDate')?.touched && frm.get('startDate')?.hasError('required')
                  ) {
                    <mat-error>Bắt buộc</mat-error>
                  }
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Đến Ngày *</mat-label>
                  <input
                    matInput
                    formControlName="endDate"
                    type="date"
                    [min]="frm.get('startDate')?.value || minDate"
                  />
                  @if (frm.get('endDate')?.touched && frm.get('endDate')?.hasError('required')) {
                    <mat-error>Bắt buộc</mat-error>
                  }
                </mat-form-field>
              </div>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Lý Do Nghỉ Phép *</mat-label>
                <textarea
                  matInput
                  formControlName="reason"
                  rows="4"
                  placeholder="Mô tả lý do nghỉ phép..."
                ></textarea>
                @if (frm.get('reason')?.touched && frm.get('reason')?.hasError('required')) {
                  <mat-error>Bắt buộc</mat-error>
                }
              </mat-form-field>
            </form>
          </mat-card-content>
          <mat-card-actions align="end" style="padding:16px">
            <button mat-button (click)="closeForm()">Hủy</button>
            <button
              mat-raised-button
              color="primary"
              (click)="submit()"
              [disabled]="frm.invalid || saving"
            >
              {{ saving ? 'Đang gửi...' : 'Gửi Đơn' }}
            </button>
          </mat-card-actions>
        </mat-card>
      </div>
    }
  `,
})
export class MyLeaveRequestsComponent implements OnInit {
  requests: LeaveRequest[] = [];
  cols = ['startDate', 'endDate', 'reason', 'createdAt', 'status', 'actions'];
  loading = false;
  page = 0;
  pageSize = 10;
  total = 0;
  showForm = false;
  frm!: FormGroup;
  saving = false;
  minDate = new Date().toISOString().split('T')[0];
  statusMap = LEAVE_STATUS_MAP;

  constructor(
    private svc: LeaveRequestService,
    private fb: FormBuilder,
    private snack: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.initForm();
    this.load();
  }

  initForm() {
    this.frm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      reason: ['', Validators.required],
    });
  }

  load() {
    this.loading = true;
    this.cdr.markForCheck();
    this.svc.getAll({ page: this.page, size: this.pageSize }).subscribe({
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

  onPage(e: PageEvent) {
    this.page = e.pageIndex;
    this.pageSize = e.pageSize;
    this.load();
  }

  openForm() {
    this.initForm();
    this.showForm = true;
    this.cdr.markForCheck();
  }

  closeForm() {
    this.showForm = false;
    this.cdr.markForCheck();
  }

  submit() {
    if (this.frm.invalid) {
      this.frm.markAllAsTouched();
      return;
    }
    const { startDate, endDate } = this.frm.value;
    if (endDate < startDate) {
      this.snack.open('Ngày kết thúc phải sau ngày bắt đầu', 'Đóng', { duration: 4000 });
      return;
    }
    this.saving = true;
    this.cdr.markForCheck();
    this.svc.create(this.frm.value).subscribe({
      next: () => {
        this.saving = false;
        this.snack.open('Đã gửi đơn nghỉ phép thành công', 'Đóng', { duration: 3000 });
        this.closeForm();
        this.load();
      },
      error: (e) => {
        this.saving = false;
        this.cdr.detectChanges();
        this.snack.open(e.error?.message || 'Có lỗi xảy ra', 'Đóng', { duration: 4000 });
      },
    });
  }

  cancel(r: LeaveRequest) {
    if (!confirm('Hủy đơn nghỉ phép này?')) return;
    this.svc.cancel(r.id).subscribe({
      next: () => {
        this.snack.open('Đã hủy đơn', 'Đóng', { duration: 3000 });
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
