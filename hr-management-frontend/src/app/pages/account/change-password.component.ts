import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AccountService } from '../../core/services/account.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatInputModule, MatFormFieldModule,
    MatButtonModule, MatIconModule, MatSnackBarModule],
  template: `
    <div class="page-header">
      <div class="page-title-group"><span class="page-eyebrow">TÀI KHOẢN</span>
        <h1>Đổi mật khẩu</h1><p>Cập nhật mật khẩu để bảo vệ tài khoản của bạn.</p>
      </div>
    </div>
    <mat-card class="form-card account-form-card">
      <mat-card-header><mat-icon mat-card-avatar>lock</mat-icon><mat-card-title>Đổi mật khẩu</mat-card-title></mat-card-header>
      <mat-card-content>
        <div class="dialog-form-grid">
          <mat-form-field appearance="outline" class="form-field-full">
            <mat-label>Mật khẩu hiện tại</mat-label>
            <input matInput type="password" [(ngModel)]="form.currentPassword" autocomplete="current-password">
          </mat-form-field>
          <mat-form-field appearance="outline" class="form-field-full">
            <mat-label>Mật khẩu mới</mat-label>
            <input matInput type="password" [(ngModel)]="form.newPassword" autocomplete="new-password">
            <mat-hint>Tối thiểu 6 ký tự</mat-hint>
          </mat-form-field>
          <mat-form-field appearance="outline" class="form-field-full">
            <mat-label>Xác nhận mật khẩu mới</mat-label>
            <input matInput type="password" [(ngModel)]="form.confirmPassword" autocomplete="new-password">
          </mat-form-field>
        </div>
      </mat-card-content>
      <mat-card-actions align="end">
        <button mat-raised-button color="primary" [disabled]="saving || !valid()" (click)="submit()">
          <mat-icon>save</mat-icon>{{ saving ? 'Đang cập nhật...' : 'Đổi mật khẩu' }}
        </button>
      </mat-card-actions>
    </mat-card>
  `
})
export class ChangePasswordComponent {
  form = { currentPassword: '', newPassword: '', confirmPassword: '' };
  saving = false;
  constructor(private account: AccountService, private snack: MatSnackBar) {}
  valid() {
    return this.form.currentPassword.trim().length > 0 && this.form.newPassword.length >= 6
      && this.form.newPassword === this.form.confirmPassword
      && this.form.currentPassword !== this.form.newPassword;
  }
  submit() {
    this.saving = true;
    this.account.changePassword(this.form).subscribe({
      next: r => { this.saving = false; this.form = { currentPassword: '', newPassword: '', confirmPassword: '' };
        this.snack.open(r.message || 'Đổi mật khẩu thành công', 'Đóng', { duration: 3000 }); },
      error: e => { this.saving = false; this.snack.open(e.error?.message || 'Không thể đổi mật khẩu', 'Đóng', { duration: 3500 }); }
    });
  }
}
