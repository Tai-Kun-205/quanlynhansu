import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="login-page">
      <div class="login-shell">
        <div class="login-visual">
          <div class="login-visual-brand"><mat-icon>business_center</mat-icon><span>Nhân Sự Pro</span></div>
          <div class="login-visual-content">
            <span class="login-kicker">NỀN TẢNG QUẢN TRỊ</span>
            <h1>Quản lý con người.<br><em>Kiến tạo thành công.</em></h1>
            <p>Tập trung dữ liệu nhân sự, tối ưu vận hành và đồng hành cùng sự phát triển của doanh nghiệp.</p>
          </div>
          <div class="login-visual-footer">Bảo mật · Hiệu quả · Minh bạch</div>
        </div>
        <mat-card class="login-card">
        <div class="login-header">
          <div class="mobile-brand"><mat-icon>business_center</mat-icon></div>
          <h2>Chào mừng trở lại</h2>
          <p>Đăng nhập để tiếp tục vào hệ thống</p>
        </div>
        <div class="login-body">
          <form [formGroup]="form" (ngSubmit)="submit()">
            @if (error) {
              <div class="error-box">
                <mat-icon style="font-size:18px;width:18px;height:18px">error_outline</mat-icon>
                {{ error }}
              </div>
            }
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Tên đăng nhập</mat-label>
              <input matInput formControlName="username" autocomplete="username">
              <mat-icon matSuffix>person</mat-icon>
              @if (form.get('username')?.touched && form.get('username')?.hasError('required')) {
                <mat-error>Vui lòng nhập tên đăng nhập</mat-error>
              }
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Mật khẩu</mat-label>
              <input matInput [type]="showPw ? 'text' : 'password'" formControlName="password" autocomplete="current-password">
              <button mat-icon-button matSuffix type="button" (click)="showPw=!showPw">
                <mat-icon>{{ showPw ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              @if (form.get('password')?.touched && form.get('password')?.hasError('required')) {
                <mat-error>Vui lòng nhập mật khẩu</mat-error>
              }
            </mat-form-field>
            <button mat-raised-button color="primary" type="submit" class="full-width"
              style="height:48px;font-size:16px;font-weight:600;margin-top:8px;border-radius:8px"
              [disabled]="loading">
              @if (loading) {
                <mat-spinner diameter="22" style="display:inline-block;margin-right:8px"></mat-spinner>
              }
              Đăng Nhập
            </button>
          </form>
        </div>
        </mat-card>
    </div>
  `
})
export class LoginComponent {
  form: FormGroup;
  loading = false;
  showPw = false;
  error = '';

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
    if (auth.isLoggedIn()) this.redirect();
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.error = '';
    this.auth.login(this.form.value).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) this.redirect();
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Tên đăng nhập hoặc mật khẩu không chính xác';
      }
    });
  }

  private redirect() {
    this.router.navigate([this.auth.isAdmin() ? '/admin/dashboard' : '/employee/profile']);
  }
}
