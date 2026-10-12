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
import { UserService } from '../../core/services/user.service';
import { User, ROLE_MAP, ACCOUNT_STATUS_MAP } from '../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, MatTableModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule,
    MatPaginatorModule, MatProgressSpinnerModule, MatCardModule],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group"><span class="page-eyebrow">HỆ THỐNG</span><h1>Quản lý tài khoản</h1><p>Kiểm soát quyền truy cập và trạng thái tài khoản người dùng.</p></div>
    </div>

    <div class="data-toolbar">
      <mat-form-field appearance="outline" class="toolbar-search">
        <mat-label>Tìm kiếm</mat-label>
        <input matInput [(ngModel)]="keyword" placeholder="Tên đăng nhập, email..." (keyup.enter)="search()">
        <button matSuffix mat-icon-button (click)="search()"><mat-icon>search</mat-icon></button>
      </mat-form-field>
      <span class="table-summary"><mat-icon>manage_accounts</mat-icon>{{ total }} tài khoản</span>
    </div>

    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="users">
            <ng-container matColumnDef="username">
              <th mat-header-cell *matHeaderCellDef>Tên Đăng Nhập</th>
              <td mat-cell *matCellDef="let u"><div class="table-person"><span class="table-avatar">{{ (u.username || '?').charAt(0).toUpperCase() }}</span><span class="table-primary">{{ u.username }}</span></div></td>
            </ng-container>
            <ng-container matColumnDef="email">
              <th mat-header-cell *matHeaderCellDef>Email</th>
              <td mat-cell *matCellDef="let u"><span class="table-muted">{{ u.email || '—' }}</span></td>
            </ng-container>
            <ng-container matColumnDef="employeeName">
              <th mat-header-cell *matHeaderCellDef>Nhân Viên</th>
              <td mat-cell *matCellDef="let u"><span class="table-primary">{{ u.employeeName || '—' }}</span></td>
            </ng-container>
            <ng-container matColumnDef="role">
              <th mat-header-cell *matHeaderCellDef style="width:140px">Vai Trò</th>
              <td mat-cell *matCellDef="let u">
                <span [style.background]="u.role==='ADMIN'?'#e0e7ff':'#f0fdf4'"
                      [style.color]="u.role==='ADMIN'?'#3730a3':'#15803d'"
                      style="padding:3px 10px;border-radius:10px;font-size:12px;font-weight:600">
                  {{ roleMap[u.role] || u.role }}
                </span>
              </td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef style="width:140px">Trạng Thái</th>
              <td mat-cell *matCellDef="let u">
                <span [class]="u.status==='ACTIVE'?'badge badge-active':'badge badge-locked'">
                  {{ statusMap[u.status] || u.status }}
                </span>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Thao Tác</th>
              <td mat-cell *matCellDef="let u">
                <div class="table-actions">
                  @if (u.status === 'ACTIVE') {
                    <button mat-icon-button color="warn" (click)="lock(u)" title="Khóa tài khoản">
                      <mat-icon>lock</mat-icon>
                    </button>
                  } @else {
                    <button mat-icon-button color="primary" (click)="unlock(u)" title="Mở khóa">
                      <mat-icon>lock_open</mat-icon>
                    </button>
                  }
                </div>
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="cols"></tr>
            <tr mat-row *matRowDef="let row; columns: cols"></tr>
          </table>
        </div>
        @if (!users.length) {
          <div class="table-empty"><mat-icon>manage_accounts</mat-icon> Không có tài khoản nào</div>
        }
        <mat-paginator [length]="total" [pageSize]="pageSize" [pageSizeOptions]="[10,20,50]" (page)="onPage($event)"></mat-paginator>
      }
    </div>
  `
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  cols = ['username', 'email', 'employeeName', 'role', 'status', 'actions'];
  loading = false; keyword = ''; page = 0; pageSize = 10; total = 0;
  roleMap = ROLE_MAP; statusMap = ACCOUNT_STATUS_MAP;

  constructor(private svc: UserService, private snack: MatSnackBar, private cdr: ChangeDetectorRef) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true; this.cdr.markForCheck();
    this.svc.getAll(this.keyword, this.page, this.pageSize).subscribe({
      next: r => { this.users = r.data.content; this.total = r.data.totalElements; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  search() { this.page = 0; this.load(); }
  onPage(e: PageEvent) { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }

  lock(u: User) {
    if (!confirm(`Khóa tài khoản "${u.username}"?`)) return;
    this.svc.lock(u.id).subscribe({
      next: () => { this.snack.open('Đã khóa tài khoản','Đóng',{duration:3000}); this.load(); },
      error: e => { this.snack.open(e.error?.message||'Có lỗi','Đóng',{duration:4000}); }
    });
  }

  unlock(u: User) {
    if (!confirm(`Mở khóa tài khoản "${u.username}"?`)) return;
    this.svc.unlock(u.id).subscribe({
      next: () => { this.snack.open('Đã mở khóa tài khoản','Đóng',{duration:3000}); this.load(); },
      error: e => { this.snack.open(e.error?.message||'Có lỗi','Đóng',{duration:4000}); }
    });
  }
}
