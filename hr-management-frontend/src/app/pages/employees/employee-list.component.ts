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
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { EmployeeService } from '../../core/services/employee.service';
import { DepartmentService } from '../../core/services/department.service';
import { PositionService } from '../../core/services/position.service';
import { Employee, EMPLOYEE_STATUS_MAP } from '../../core/models/employee.model';
import { Department } from '../../core/models/department.model';
import { Position } from '../../core/models/position.model';
import { ExportService } from '../../core/services/export.service';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule,
    MatPaginatorModule, MatProgressSpinnerModule, MatTooltipModule, MatCardModule],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group">
        <span class="page-eyebrow">NHÂN SỰ</span>
        <h1>Quản lý nhân viên</h1>
        <p>Quản lý hồ sơ, phòng ban, chức vụ và trạng thái làm việc.</p>
      </div>
      <div class="page-header-actions">
        <button mat-stroked-button (click)="exportExcel()" matTooltip="Xuất danh sách nhân viên">
          <mat-icon>file_download</mat-icon> Xuất Excel
        </button>
        <button mat-raised-button color="primary" (click)="openForm()">
          <mat-icon>person_add</mat-icon> Thêm Nhân Viên
        </button>
      </div>
    </div>

    <div class="data-toolbar">
      <mat-form-field appearance="outline" class="toolbar-search">
        <mat-label>Tìm kiếm</mat-label>
        <input matInput [(ngModel)]="keyword" placeholder="Mã, tên, email, SĐT..." (keyup.enter)="search()">
        <button matSuffix mat-icon-button (click)="search()"><mat-icon>search</mat-icon></button>
      </mat-form-field>
      <mat-form-field appearance="outline" class="toolbar-filter">
        <mat-label>Phòng Ban</mat-label>
        <mat-select [(ngModel)]="filterDeptId" (ngModelChange)="search()">
          <mat-option [value]="null">Tất cả</mat-option>
          @for (d of departments; track d.id) { <mat-option [value]="d.id">{{ d.name }}</mat-option> }
        </mat-select>
      </mat-form-field>
      <mat-form-field appearance="outline" class="toolbar-filter">
        <mat-label>Chức Vụ</mat-label>
        <mat-select [(ngModel)]="filterPosId" (ngModelChange)="search()">
          <mat-option [value]="null">Tất cả</mat-option>
          @for (p of positions; track p.id) { <mat-option [value]="p.id">{{ p.name }}</mat-option> }
        </mat-select>
      </mat-form-field>
      <mat-form-field appearance="outline" class="toolbar-filter">
        <mat-label>Trạng Thái</mat-label>
        <mat-select [(ngModel)]="filterStatus" (ngModelChange)="search()">
          <mat-option value="">Tất cả</mat-option>
          <mat-option value="WORKING">Đang Làm Việc</mat-option>
          <mat-option value="ON_LEAVE">Tạm Nghỉ</mat-option>
          <mat-option value="RESIGNED">Đã Nghỉ Việc</mat-option>
        </mat-select>
      </mat-form-field>
      <span class="table-summary"><mat-icon>groups</mat-icon>{{ total }} nhân viên</span>
    </div>

    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="employees">
            <ng-container matColumnDef="code">
              <th mat-header-cell *matHeaderCellDef style="width:100px">Mã NV</th>
              <td mat-cell *matCellDef="let e"><span class="table-code">{{ e.code }}</span></td>
            </ng-container>
            <ng-container matColumnDef="fullName">
              <th mat-header-cell *matHeaderCellDef>Họ và Tên</th>
              <td mat-cell *matCellDef="let e">
                <div class="table-person">
                  <span class="table-avatar">{{ (e.fullName || '?').charAt(0).toUpperCase() }}</span>
                  <span class="table-primary">{{ e.fullName }}</span>
                </div>
              </td>
            </ng-container>
            <ng-container matColumnDef="department">
              <th mat-header-cell *matHeaderCellDef>Phòng Ban</th>
              <td mat-cell *matCellDef="let e"><div class="table-entity"><mat-icon class="table-entity-icon">corporate_fare</mat-icon><span>{{ e.departmentName || '—' }}</span></div></td>
            </ng-container>
            <ng-container matColumnDef="position">
              <th mat-header-cell *matHeaderCellDef>Chức Vụ</th>
              <td mat-cell *matCellDef="let e"><span class="table-muted">{{ e.positionName || '—' }}</span></td>
            </ng-container>
            <ng-container matColumnDef="phone">
              <th mat-header-cell *matHeaderCellDef style="width:130px">SĐT</th>
              <td mat-cell *matCellDef="let e"><span class="table-muted">{{ e.phone || '—' }}</span></td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef style="width:140px">Trạng Thái</th>
              <td mat-cell *matCellDef="let e"><span [class]="getEmpBadge(e.status)">{{ statusMap[e.status] || e.status }}</span></td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef style="width:100px">Thao Tác</th>
              <td mat-cell *matCellDef="let e">
                <div class="table-actions">
                  <button mat-icon-button color="primary" (click)="openForm(e)" matTooltip="Chỉnh sửa"><mat-icon>edit</mat-icon></button>
                  <button mat-icon-button color="warn" (click)="delete(e)" matTooltip="Xóa"><mat-icon>delete</mat-icon></button>
                </div>
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="cols"></tr>
            <tr mat-row *matRowDef="let row; columns: cols"></tr>
          </table>
        </div>
        @if (!employees.length) {
          <div class="table-empty"><mat-icon>people_outline</mat-icon> Không tìm thấy nhân viên nào</div>
        }
        <mat-paginator [length]="total" [pageSize]="pageSize" [pageSizeOptions]="[5,10,20,50]" (page)="onPage($event)"></mat-paginator>
      }
    </div>

    @if (showForm) {
      <div class="dialog-overlay" (click)="closeForm()">
        <mat-card class="dialog-card" style="max-width:640px" (click)="$event.stopPropagation()">
          <mat-card-header><mat-card-title>{{ editItem ? 'Chỉnh Sửa Nhân Viên' : 'Thêm Nhân Viên Mới' }}</mat-card-title></mat-card-header>
          <mat-card-content style="padding:20px 16px 0">
            <form [formGroup]="frm">
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Mã Nhân Viên *</mat-label>
                  <input matInput formControlName="code" [readonly]="!!editItem">
                  @if (frm.get('code')?.touched && frm.get('code')?.hasError('required')) { <mat-error>Bắt buộc</mat-error> }
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Họ và Tên *</mat-label>
                  <input matInput formControlName="fullName">
                  @if (frm.get('fullName')?.touched && frm.get('fullName')?.hasError('required')) { <mat-error>Bắt buộc</mat-error> }
                </mat-form-field>
              </div>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Email</mat-label>
                  <input matInput formControlName="email" type="email">
                  @if (frm.get('email')?.touched && frm.get('email')?.hasError('email')) { <mat-error>Email không hợp lệ</mat-error> }
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Số Điện Thoại</mat-label>
                  <input matInput formControlName="phone">
                </mat-form-field>
              </div>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Giới Tính</mat-label>
                  <mat-select formControlName="gender">
                    <mat-option value="MALE">Nam</mat-option>
                    <mat-option value="FEMALE">Nữ</mat-option>
                    <mat-option value="OTHER">Khác</mat-option>
                  </mat-select>
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Ngày Sinh</mat-label>
                  <input matInput formControlName="dateOfBirth" type="date">
                </mat-form-field>
              </div>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Ngày Vào Làm *</mat-label>
                  <input matInput formControlName="startDate" type="date">
                  @if (frm.get('startDate')?.touched && frm.get('startDate')?.hasError('required')) { <mat-error>Bắt buộc</mat-error> }
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Trạng Thái</mat-label>
                  <mat-select formControlName="status">
                    <mat-option value="WORKING">Đang Làm Việc</mat-option>
                    <mat-option value="ON_LEAVE">Tạm Nghỉ</mat-option>
                    <mat-option value="RESIGNED">Đã Nghỉ Việc</mat-option>
                  </mat-select>
                </mat-form-field>
              </div>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Phòng Ban *</mat-label>
                  <mat-select formControlName="departmentId">
                    @for (d of departments; track d.id) { <mat-option [value]="d.id">{{ d.name }}</mat-option> }
                  </mat-select>
                  @if (frm.get('departmentId')?.touched && frm.get('departmentId')?.hasError('required')) { <mat-error>Bắt buộc</mat-error> }
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Chức Vụ *</mat-label>
                  <mat-select formControlName="positionId">
                    @for (p of positions; track p.id) { <mat-option [value]="p.id">{{ p.name }}</mat-option> }
                  </mat-select>
                  @if (frm.get('positionId')?.touched && frm.get('positionId')?.hasError('required')) { <mat-error>Bắt buộc</mat-error> }
                </mat-form-field>
              </div>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Địa Chỉ</mat-label>
                <input matInput formControlName="address">
              </mat-form-field>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Đường dẫn ảnh đại diện</mat-label>
                  <input matInput formControlName="avatarUrl" placeholder="https://...">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Trình độ học vấn</mat-label>
                  <input matInput formControlName="educationLevel">
                </mat-form-field>
              </div>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Chuyên ngành</mat-label>
                  <input matInput formControlName="major">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Người liên hệ khẩn cấp</mat-label>
                  <input matInput formControlName="emergencyContactName">
                </mat-form-field>
              </div>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>SĐT liên hệ khẩn cấp</mat-label>
                  <input matInput formControlName="emergencyContactPhone">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Mối quan hệ</mat-label>
                  <input matInput formControlName="emergencyContactRelationship">
                </mat-form-field>
              </div>
              @if (!editItem) {
                <div class="form-row">
                  <mat-form-field appearance="outline">
                    <mat-label>Tên Đăng Nhập</mat-label>
                    <input matInput formControlName="username">
                  </mat-form-field>
                  <mat-form-field appearance="outline">
                    <mat-label>Mật Khẩu</mat-label>
                    <input matInput formControlName="password" type="password" placeholder="Mặc định: 123456">
                  </mat-form-field>
                </div>
              }
            </form>
          </mat-card-content>
          <mat-card-actions align="end" style="padding:16px">
            <button mat-button (click)="closeForm()">Hủy</button>
            <button mat-raised-button color="primary" (click)="save()" [disabled]="frm.invalid || saving">{{ saving ? 'Đang lưu...' : 'Lưu' }}</button>
          </mat-card-actions>
        </mat-card>
      </div>
    }
  `
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  departments: Department[] = [];
  positions: Position[] = [];
  cols = ['code', 'fullName', 'department', 'position', 'phone', 'status', 'actions'];
  loading = false; keyword = ''; filterDeptId: number | null = null; filterPosId: number | null = null; filterStatus = '';
  page = 0; pageSize = 10; total = 0; showForm = false; editItem: Employee | null = null;
  frm!: FormGroup; saving = false; statusMap = EMPLOYEE_STATUS_MAP;

  constructor(private svc: EmployeeService, private deptSvc: DepartmentService, private posSvc: PositionService,
    private exportSvc: ExportService, private fb: FormBuilder, private snack: MatSnackBar, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.initForm();
    this.deptSvc.getAll().subscribe(r => { this.departments = r.data; this.cdr.markForCheck(); });
    this.posSvc.getAll().subscribe(r => { this.positions = r.data; this.cdr.markForCheck(); });
    this.load();
  }

  initForm(e?: Employee) {
    this.frm = this.fb.group({
      code: [e?.code||'', Validators.required], fullName: [e?.fullName||'', Validators.required],
      email: [e?.email||'', [Validators.email]], phone: [e?.phone||''], gender: [e?.gender||''],
      dateOfBirth: [e?.dateOfBirth||''], startDate: [e?.startDate||'', Validators.required],
      departmentId: [e?.departmentId||null, Validators.required], positionId: [e?.positionId||null, Validators.required],
      status: [e?.status||'WORKING'], address: [e?.address||''], avatarUrl: [e?.avatarUrl||''],
      emergencyContactName: [e?.emergencyContactName||''], emergencyContactPhone: [e?.emergencyContactPhone||''],
      emergencyContactRelationship: [e?.emergencyContactRelationship||''], educationLevel: [e?.educationLevel||''],
      major: [e?.major||''], username: [''], password: ['']
    });
  }

  load() {
    this.loading = true; this.cdr.markForCheck();
    this.svc.getAll({ keyword: this.keyword, departmentId: this.filterDeptId, positionId: this.filterPosId, status: this.filterStatus, page: this.page, size: this.pageSize }).subscribe({
      next: r => { this.employees = r.data.content; this.total = r.data.totalElements; this.loading = false; this.cdr.detectChanges(); },
      error: e => {
        this.loading = false;
        this.cdr.detectChanges();
        this.snack.open(e.error?.message || 'Không thể tải danh sách nhân viên. Hãy kiểm tra backend và đăng nhập lại.', 'Đóng', { duration: 5000 });
      }
    });
  }

  search() { this.page = 0; this.load(); }
  onPage(e: PageEvent) { this.page = e.pageIndex; this.pageSize = e.pageSize; this.load(); }
  exportExcel() {
    this.exportSvc.employees({ keyword: this.keyword, departmentId: this.filterDeptId, positionId: this.filterPosId, status: this.filterStatus }).subscribe({
      next: file => this.download(file, 'danh-sach-nhan-vien.xlsx'),
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
  openForm(e?: Employee) { this.editItem = e||null; this.initForm(e); this.showForm = true; this.cdr.markForCheck(); }
  closeForm() { this.showForm = false; this.editItem = null; this.cdr.markForCheck(); }

  save() {
    if (this.frm.invalid) { this.frm.markAllAsTouched(); return; }
    this.saving = true;
    const obs = this.editItem ? this.svc.update(this.editItem.id, this.frm.value) : this.svc.create(this.frm.value);
    obs.subscribe({
      next: r => { this.saving = false; this.snack.open(r.message||'Thành công','Đóng',{duration:3000}); this.closeForm(); this.load(); },
      error: e => { this.saving = false; this.cdr.detectChanges(); this.snack.open(e.error?.message||'Có lỗi','Đóng',{duration:4000}); }
    });
  }

  delete(e: Employee) {
    if (!confirm(`Xóa nhân viên "${e.fullName}"?`)) return;
    this.svc.delete(e.id).subscribe({
      next: r => { this.snack.open(r.message||'Đã xóa','Đóng',{duration:3000}); this.load(); },
      error: err => { this.snack.open(err.error?.message||'Không thể xóa','Đóng',{duration:4000}); }
    });
  }

  getEmpBadge(s: string) {
    const m: Record<string,string> = { WORKING:'badge badge-working', ON_LEAVE:'badge badge-on_leave', RESIGNED:'badge badge-resigned' };
    return m[s] || 'badge';
  }
}
