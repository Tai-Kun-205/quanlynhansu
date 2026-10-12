import { Component, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { EmployeeService } from '../../core/services/employee.service';
import { AuthService } from '../../core/services/auth.service';
import { Employee, GENDER_MAP, EMPLOYEE_STATUS_MAP } from '../../core/models/employee.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule,
    MatProgressSpinnerModule, MatCardModule],
  template: `
    <div class="profile-wrapper">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else if (emp) {
        <div class="profile-header-card">
          <div class="profile-avatar">
            @if (emp.avatarUrl) { <img [src]="emp.avatarUrl" alt="Ảnh đại diện"> } @else { <mat-icon>person</mat-icon> }
          </div>
          <div>
            <div class="profile-name">{{ emp.fullName }}</div>
            <div class="profile-sub">{{ emp.positionName }} · {{ emp.departmentName }}</div>
            <div style="margin-top:6px">
              <span [class]="getEmpBadge(emp.status)">{{ statusMap[emp.status] || emp.status }}</span>
            </div>
          </div>
        </div>

        <!-- Thông tin cơ bản -->
        <div class="info-card">
          <h3>Thông Tin Cơ Bản</h3>
          <div class="info-grid">
            <div class="info-field"><label>Mã Nhân Viên</label><span>{{ emp.code }}</span></div>
            <div class="info-field"><label>Phòng Ban</label><span>{{ emp.departmentName }}</span></div>
            <div class="info-field"><label>Chức Vụ</label><span>{{ emp.positionName }}</span></div>
            <div class="info-field"><label>Ngày Vào Làm</label><span>{{ emp.startDate }}</span></div>
            <div class="info-field"><label>Thâm Niên</label><span>{{ emp.seniorityYears ?? 0 }} năm</span></div>
          </div>
        </div>

        <!-- Thông tin cá nhân - có thể chỉnh sửa -->
        <div class="info-card">
          <h3 style="display:flex;justify-content:space-between;align-items:center">
            Thông Tin Cá Nhân
            @if (!editing) {
              <button mat-stroked-button color="primary" (click)="startEdit()" style="font-size:13px">
                <mat-icon style="font-size:16px;width:16px;height:16px;margin-right:4px">edit</mat-icon> Chỉnh Sửa
              </button>
            }
          </h3>
          @if (!editing) {
            <div class="info-grid">
              <div class="info-field"><label>Họ và Tên</label><span>{{ emp.fullName }}</span></div>
              <div class="info-field"><label>Giới Tính</label><span>{{ genderMap[emp.gender || ''] || '—' }}</span></div>
              <div class="info-field"><label>Ngày Sinh</label><span>{{ emp.dateOfBirth || '—' }}</span></div>
              <div class="info-field"><label>Số Điện Thoại</label><span>{{ emp.phone || '—' }}</span></div>
              <div class="info-field" style="grid-column:1/-1"><label>Địa Chỉ</label><span>{{ emp.address || '—' }}</span></div>
            </div>
            <h4>Học vấn</h4>
            <div class="info-grid">
              <div class="info-field"><label>Trình độ</label><span>{{ emp.educationLevel || '—' }}</span></div>
              <div class="info-field"><label>Chuyên ngành</label><span>{{ emp.major || '—' }}</span></div>
            </div>
            <h4>Liên hệ khẩn cấp</h4>
            <div class="info-grid">
              <div class="info-field"><label>Họ tên</label><span>{{ emp.emergencyContactName || '—' }}</span></div>
              <div class="info-field"><label>Số điện thoại</label><span>{{ emp.emergencyContactPhone || '—' }}</span></div>
              <div class="info-field"><label>Mối quan hệ</label><span>{{ emp.emergencyContactRelationship || '—' }}</span></div>
            </div>
          } @else {
            <form [formGroup]="frm">
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Họ và Tên</mat-label>
                  <input matInput formControlName="fullName">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Giới Tính</mat-label>
                  <mat-select formControlName="gender">
                    <mat-option value="MALE">Nam</mat-option>
                    <mat-option value="FEMALE">Nữ</mat-option>
                    <mat-option value="OTHER">Khác</mat-option>
                  </mat-select>
                </mat-form-field>
              </div>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Ngày Sinh</mat-label>
                  <input matInput formControlName="dateOfBirth" type="date">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Số Điện Thoại</mat-label>
                  <input matInput formControlName="phone">
                </mat-form-field>
              </div>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Địa Chỉ</mat-label>
                <input matInput formControlName="address">
              </mat-form-field>
              <div class="form-row">
                <mat-form-field appearance="outline">
                  <mat-label>Đường dẫn ảnh đại diện</mat-label>
                  <input matInput formControlName="avatarUrl">
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
              <div style="display:flex;gap:12px;justify-content:flex-end;margin-top:8px">
                <button mat-button (click)="cancelEdit()">Hủy</button>
                <button mat-raised-button color="primary" (click)="saveProfile()" [disabled]="saving">
                  {{ saving ? 'Đang lưu...' : 'Lưu Thay Đổi' }}
                </button>
              </div>
            </form>
          }
        </div>

        <!-- Tài khoản -->
        <div class="info-card">
          <h3>Tài Khoản</h3>
          <div class="info-grid">
            <div class="info-field"><label>Tên Đăng Nhập</label><span>{{ emp.username || '—' }}</span></div>
            <div class="info-field"><label>Email</label><span>{{ emp.email || '—' }}</span></div>
          </div>
        </div>
      } @else {
        <div class="info-card" style="text-align:center;padding:40px;color:#718096">
          <mat-icon style="font-size:48px;width:48px;height:48px;display:block;margin:0 auto 12px">error_outline</mat-icon>
          Không thể tải thông tin cá nhân
        </div>
      }
    </div>
  `
})
export class ProfileComponent implements OnInit {
  emp: Employee | null = null;
  loading = true;
  editing = false;
  saving = false;
  frm!: FormGroup;
  genderMap = GENDER_MAP;
  statusMap = EMPLOYEE_STATUS_MAP;

  constructor(private empSvc: EmployeeService, private auth: AuthService, private fb: FormBuilder, private snack: MatSnackBar, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    const empId = this.auth.currentUser()?.employeeId;
    if (!empId) { this.loading = false; return; }
    
    this.cdr.markForCheck();
    this.empSvc.getById(empId).subscribe({
      next: r => { this.emp = r.data; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  startEdit() {
    this.frm = this.fb.group({
      fullName: [this.emp?.fullName || ''],
      gender: [this.emp?.gender || ''],
      dateOfBirth: [this.emp?.dateOfBirth || ''],
      phone: [this.emp?.phone || ''],
      address: [this.emp?.address || ''],
      avatarUrl: [this.emp?.avatarUrl || ''],
      emergencyContactName: [this.emp?.emergencyContactName || ''],
      emergencyContactPhone: [this.emp?.emergencyContactPhone || ''],
      emergencyContactRelationship: [this.emp?.emergencyContactRelationship || ''],
      educationLevel: [this.emp?.educationLevel || ''],
      major: [this.emp?.major || '']
    });
    this.editing = true;
    this.cdr.markForCheck();
  }

  cancelEdit() { 
    this.editing = false; 
    this.cdr.markForCheck();
  }

  saveProfile() {
    if (!this.emp) return;
    this.saving = true;
    this.cdr.markForCheck();
    this.empSvc.updateProfile(this.emp.id, this.frm.value).subscribe({
      next: r => {
        this.emp = r.data;
        this.saving = false;
        this.editing = false;
        this.cdr.detectChanges();
        this.snack.open('Cập nhật thành công', 'Đóng', { duration: 3000 });
      },
      error: e => { 
        this.saving = false; 
        this.cdr.detectChanges();
        this.snack.open(e.error?.message || 'Có lỗi', 'Đóng', { duration: 4000 }); 
      }
    });
  }

  getEmpBadge(status: string) {
    const m: Record<string, string> = { WORKING: 'badge badge-working', ON_LEAVE: 'badge badge-on_leave', RESIGNED: 'badge badge-resigned' };
    return m[status] || 'badge';
  }
}
