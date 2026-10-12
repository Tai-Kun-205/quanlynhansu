import { Component, HostListener, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, MatSidenavModule, MatToolbarModule,
    MatIconModule, MatButtonModule, MatMenuModule, MatDividerModule, MatBadgeModule],
  template: `
    <mat-sidenav-container class="sidenav-container">
      <mat-sidenav #sidenav [mode]="isMobile ? 'over' : 'side'" [(opened)]="sidenavOpened" class="sidenav">
        <div class="sidenav-header">
          <div class="brand-mark"><mat-icon class="logo-icon">business_center</mat-icon></div>
          <h2>Nhân Sự Pro</h2>
          <p>Hệ thống quản trị doanh nghiệp</p>
        </div>
        <div class="nav-caption">TỔNG QUAN</div>
        <nav class="sidebar-nav">
          <a routerLink="/admin/dashboard" routerLinkActive="active" class="nav-item">
            <mat-icon>space_dashboard</mat-icon><span>Tổng quan</span>
          </a>
          <a routerLink="/admin/reports" routerLinkActive="active" class="nav-item">
            <mat-icon>analytics</mat-icon><span>Báo cáo</span>
          </a>
          <div class="nav-caption">QUẢN LÝ NHÂN SỰ</div>
          <a routerLink="/admin/employees" routerLinkActive="active" class="nav-item">
            <mat-icon>groups</mat-icon><span>Nhân viên</span>
          </a>
          <a routerLink="/admin/departments" routerLinkActive="active" class="nav-item">
            <mat-icon>corporate_fare</mat-icon><span>Phòng ban</span>
          </a>
          <a routerLink="/admin/positions" routerLinkActive="active" class="nav-item">
            <mat-icon>badge</mat-icon><span>Chức vụ</span>
          </a>
          <div class="nav-caption">VẬN HÀNH</div>
          <a routerLink="/admin/attendances" routerLinkActive="active" class="nav-item">
            <mat-icon>schedule</mat-icon><span>Chấm công</span>
          </a>
          <a routerLink="/admin/work-shifts" routerLinkActive="active" class="nav-item">
            <mat-icon>date_range</mat-icon><span>Ca làm việc</span>
          </a>
          <a routerLink="/admin/notifications" routerLinkActive="active" class="nav-item">
            <mat-icon>notifications</mat-icon><span>Thông báo</span>
          </a>
          <a routerLink="/admin/activity-logs" routerLinkActive="active" class="nav-item">
            <mat-icon>history</mat-icon><span>Nhật ký hoạt động</span>
          </a>
          <a routerLink="/admin/payrolls" routerLinkActive="active" class="nav-item">
            <mat-icon>payments</mat-icon><span>Bảng lương</span>
          </a>
          <a routerLink="/admin/leave-requests" routerLinkActive="active" class="nav-item">
            <mat-icon>event_available</mat-icon><span>Nghỉ phép</span>
          </a>
          <a routerLink="/admin/users" routerLinkActive="active" class="nav-item">
            <mat-icon>manage_accounts</mat-icon><span>Tài khoản</span>
          </a>
        </nav>
      </mat-sidenav>

      <mat-sidenav-content style="display:flex;flex-direction:column;">
        <mat-toolbar class="main-toolbar">
          <button mat-icon-button (click)="sidenav.toggle()" title="Mở/đóng menu">
            <mat-icon>menu</mat-icon>
          </button>
          <div class="topbar-context">
            <span class="topbar-eyebrow">CỔNG QUẢN TRỊ</span>
            <strong>Quản lý nhân sự</strong>
          </div>
          <span class="spacer"></span>
          <a mat-icon-button routerLink="/admin/notifications" [matBadge]="unreadCount || null"
             matBadgeColor="warn" matBadgeSize="small" title="Thông báo chưa đọc">
            <mat-icon>notifications</mat-icon>
          </a>
          <div class="topbar-user">
            <span class="user-avatar">{{ (auth.currentUser()?.username || 'A').charAt(0).toUpperCase() }}</span>
            <span class="toolbar-username">{{ auth.currentUser()?.username }}</span>
          </div>
          <button mat-icon-button [matMenuTriggerFor]="menu">
            <mat-icon>account_circle</mat-icon>
          </button>
          <mat-menu #menu="matMenu">
            <button mat-menu-item routerLink="/admin/account/password">
              <mat-icon>lock_reset</mat-icon> Đổi mật khẩu
            </button>
            <button mat-menu-item (click)="logout()">
              <mat-icon>logout</mat-icon> Đăng Xuất
            </button>
          </mat-menu>
        </mat-toolbar>
        <div class="main-content">
          <router-outlet></router-outlet>
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `
})
export class AdminLayoutComponent implements OnInit {
  auth = inject(AuthService);
  unreadCount = 0;
  isMobile = typeof window !== 'undefined' && window.innerWidth <= 800;
  sidenavOpened = !this.isMobile;
  constructor(private notifications: NotificationService) {}

  ngOnInit() {
    this.notifications.my().subscribe({
      next: r => this.unreadCount = r.data.filter(item => !item.read).length,
      error: () => this.unreadCount = 0
    });
  }

  @HostListener('window:resize')
  onResize() {
    const mobile = window.innerWidth <= 800;
    if (mobile !== this.isMobile) {
      this.isMobile = mobile;
      this.sidenavOpened = !mobile;
    }
  }

  logout() { this.auth.logout(); }
}
