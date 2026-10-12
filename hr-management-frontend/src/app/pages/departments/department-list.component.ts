import { Component, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { DepartmentService } from '../../core/services/department.service';
import { Department, DEPT_STATUS_MAP } from '../../core/models/department.model';

@Component({
  selector: 'app-department-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule,
    MatProgressSpinnerModule, MatTooltipModule, MatCardModule],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group"><span class="page-eyebrow">TỔ CHỨC</span><h1>Quản lý phòng ban</h1><p>Quản lý các đơn vị và cơ cấu tổ chức trong doanh nghiệp.</p></div>
      <div class="page-header-actions"><button mat-raised-button color="primary" (click)="openForm()"><mat-icon>add</mat-icon> Thêm phòng ban</button></div>
    </div>

    <div class="data-toolbar">
      <mat-form-field appearance="outline" class="toolbar-search">
        <mat-label>Tìm kiếm</mat-label>
        <input matInput [(ngModel)]="keyword" placeholder="Tên, mã phòng ban..." (keyup.enter)="search()">
        <button matSuffix mat-icon-button (click)="search()"><mat-icon>search</mat-icon></button>
      </mat-form-field>
      <span class="table-summary"><mat-icon>corporate_fare</mat-icon>{{ departments.length }} phòng ban</span>
    </div>

    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="departments">
            <ng-container matColumnDef="code">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Mã PB</th>
              <td mat-cell *matCellDef="let d"><span class="table-code">{{ d.code }}</span></td>
            </ng-container>
            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Tên Phòng Ban</th>
              <td mat-cell *matCellDef="let d"><div class="table-entity"><mat-icon class="table-entity-icon">corporate_fare</mat-icon><span class="table-primary">{{ d.name }}</span></div></td>
            </ng-container>
            <ng-container matColumnDef="description">
              <th mat-header-cell *matHeaderCellDef>Mô Tả</th>
              <td mat-cell *matCellDef="let d"><span class="table-muted">{{ d.description || '—' }}</span></td>
            </ng-container>
            <ng-container matColumnDef="employeeCount">
              <th mat-header-cell *matHeaderCellDef style="width:120px;text-align:center">Nhân Viên</th>
              <td mat-cell *matCellDef="let d" style="text-align:center">
                <span class="table-code">{{ d.employeeCount }} nhân viên</span>
              </td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef style="width:140px">Trạng Thái</th>
              <td mat-cell *matCellDef="let d">
                <span [class]="getBadgeClass(d.status)">{{ statusMap[d.status] || d.status }}</span>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef style="width:100px">Thao Tác</th>
              <td mat-cell *matCellDef="let d">
                <div class="table-actions">
                  <button mat-icon-button color="primary" (click)="openForm(d)" matTooltip="Chỉnh sửa">
                    <mat-icon>edit</mat-icon>
                  </button>
                  <button mat-icon-button color="warn" (click)="delete(d)" matTooltip="Xóa">
                    <mat-icon>delete</mat-icon>
                  </button>
                </div>
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="cols"></tr>
            <tr mat-row *matRowDef="let row; columns: cols"></tr>
          </table>
        </div>
        @if (!departments.length) {
          <div class="table-empty">
            <mat-icon>inbox</mat-icon>
            Chưa có phòng ban nào
          </div>
        }
      }
    </div>

    @if (showForm) {
      <div class="dialog-overlay" (click)="closeForm()">
        <mat-card class="dialog-card" (click)="$event.stopPropagation()">
          <mat-card-header>
            <mat-card-title>{{ editItem ? 'Chỉnh Sửa Phòng Ban' : 'Thêm Phòng Ban' }}</mat-card-title>
          </mat-card-header>
          <mat-card-content style="padding:20px 16px 0">
            <form [formGroup]="frm">
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Mã Phòng Ban *</mat-label>
                <input matInput formControlName="code" [readonly]="!!editItem">
                @if (frm.get('code')?.touched && frm.get('code')?.hasError('required')) {
                  <mat-error>Bắt buộc</mat-error>
                }
              </mat-form-field>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Tên Phòng Ban *</mat-label>
                <input matInput formControlName="name">
                @if (frm.get('name')?.touched && frm.get('name')?.hasError('required')) {
                  <mat-error>Bắt buộc</mat-error>
                }
              </mat-form-field>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Mô Tả</mat-label>
                <textarea matInput formControlName="description" rows="2"></textarea>
              </mat-form-field>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Trạng Thái</mat-label>
                <mat-select formControlName="status">
                  <mat-option value="ACTIVE">Đang Hoạt Động</mat-option>
                  <mat-option value="INACTIVE">Ngừng Hoạt Động</mat-option>
                </mat-select>
              </mat-form-field>
            </form>
          </mat-card-content>
          <mat-card-actions align="end" style="padding:16px">
            <button mat-button (click)="closeForm()">Hủy</button>
            <button mat-raised-button color="primary" (click)="save()" [disabled]="frm.invalid || saving">
              {{ saving ? 'Đang lưu...' : 'Lưu' }}
            </button>
          </mat-card-actions>
        </mat-card>
      </div>
    }
  `
})
export class DepartmentListComponent implements OnInit {
  departments: Department[] = [];
  cols = ['code', 'name', 'description', 'employeeCount', 'status', 'actions'];
  loading = false;
  keyword = '';
  showForm = false;
  editItem: Department | null = null;
  frm!: FormGroup;
  saving = false;
  statusMap = DEPT_STATUS_MAP;

  constructor(private svc: DepartmentService, private fb: FormBuilder, private snack: MatSnackBar, private cdr: ChangeDetectorRef) {}

  ngOnInit() { this.initForm(); this.load(); }

  initForm(d?: Department) {
    this.frm = this.fb.group({
      code: [d?.code || '', Validators.required],
      name: [d?.name || '', Validators.required],
      description: [d?.description || ''],
      status: [d?.status || 'ACTIVE']
    });
  }

  load() {
    this.loading = true;
    this.cdr.markForCheck();
    this.svc.getAll(this.keyword || undefined).subscribe({
      next: r => { this.departments = r.data; this.loading = false; this.cdr.detectChanges(); },
      error: e => {
        this.loading = false;
        this.cdr.detectChanges();
        this.snack.open(e.error?.message || 'Không thể tải danh sách phòng ban. Hãy kiểm tra backend và đăng nhập lại.', 'Đóng', { duration: 5000 });
      }
    });
  }

  search() { this.load(); }
  openForm(d?: Department) { this.editItem = d || null; this.initForm(d); this.showForm = true; this.cdr.markForCheck(); }
  closeForm() { this.showForm = false; this.editItem = null; this.cdr.markForCheck(); }

  save() {
    if (this.frm.invalid) { this.frm.markAllAsTouched(); return; }
    this.saving = true;
    const obs = this.editItem
      ? this.svc.update(this.editItem.id, this.frm.value)
      : this.svc.create(this.frm.value);
    obs.subscribe({
      next: r => { this.saving = false; this.snack.open(r.message || 'Thành công', 'Đóng', { duration: 3000 }); this.closeForm(); this.load(); },
      error: e => { this.saving = false; this.cdr.detectChanges(); this.snack.open(e.error?.message || 'Có lỗi xảy ra', 'Đóng', { duration: 4000 }); }
    });
  }

  delete(d: Department) {
    if (!confirm(`Xóa phòng ban "${d.name}"?`)) return;
    this.svc.delete(d.id).subscribe({
      next: r => { this.snack.open(r.message || 'Đã xóa', 'Đóng', { duration: 3000 }); this.load(); },
      error: e => { this.snack.open(e.error?.message || 'Không thể xóa', 'Đóng', { duration: 4000 }); }
    });
  }

  getBadgeClass(status: string) {
    return status === 'ACTIVE' ? 'badge badge-active' : 'badge badge-inactive';
  }
}
