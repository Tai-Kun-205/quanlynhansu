import { Component, HostListener, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-employee-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, MatSidenavModule, MatToolbarModule,
    MatIconModule, MatButtonModule, MatMenuModule, MatBadgeModule],
  template: `
    <mat-sidenav-container class="sidenav-container">
      <mat-sidenav #sidenav [mode]="isMobile ? 'over' : 'side'" [(opened)]="sidenavOpened" class="sidenav">
        <div class="sidenav-header">
          <div class="brand-mark"><mat-icon class="logo-icon">person</mat-icon></div>
          <h2>Nhân Sự Pro</h2>
          <p>Khu vực nhân viên</p>
        </div>
        <div class="nav-caption">KHU VỰC CÁ NHÂN</div>
        <nav class="sidebar-nav">
          <a routerLink="/employee/profile" routerLinkActive="active" class="nav-item">
            <mat-icon>account_circle</mat-icon><span>Hồ sơ cá nhân</span>
          </a>
          <a routerLink="/employee/attendance" routerLinkActive="active" class="nav-item">
            <mat-icon>schedule</mat-icon><span>Chấm công</span>
          </a>
          <a routerLink="/employee/work-shifts" routerLinkActive="active" class="nav-item">
            <mat-icon>date_range</mat-icon><span>Lịch làm việc</span>
          </a>
          <a routerLink="/employee/notifications" routerLinkActive="active" class="nav-item">
            <mat-icon>notifications</mat-icon><span>Thông báo</span>
          </a>
          <a routerLink="/employee/payrolls" routerLinkActive="active" class="nav-item">
            <mat-icon>payments</mat-icon><span>Lương của tôi</span>
          </a>
          <a routerLink="/employee/leave-requests" routerLinkActive="active" class="nav-item">
            <mat-icon>event_available</mat-icon><span>Đơn nghỉ phép</span>
          </a>
        </nav>
      </mat-sidenav>

      <mat-sidenav-content style="display:flex;flex-direction:column;">
        <mat-toolbar class="main-toolbar">
          <button mat-icon-button (click)="sidenav.toggle()">
            <mat-icon>menu</mat-icon>
          </button>
          <div class="topbar-context">
            <span class="topbar-eyebrow">KHU VỰC NHÂN VIÊN</span>
            <strong>Không gian làm việc</strong>
          </div>
          <span class="spacer"></span>
          <a mat-icon-button routerLink="/employee/notifications" [matBadge]="unreadCount || null"
             matBadgeColor="warn" matBadgeSize="small" title="Thông báo chưa đọc">
            <mat-icon>notifications</mat-icon>
          </a>
          <div class="topbar-user">
            <span class="user-avatar">{{ (auth.currentUser()?.username || 'N').charAt(0).toUpperCase() }}</span>
            <span class="toolbar-username">{{ auth.currentUser()?.username }}</span>
          </div>
          <button mat-icon-button [matMenuTriggerFor]="menu">
            <mat-icon>account_circle</mat-icon>
          </button>
          <mat-menu #menu="matMenu">
            <button mat-menu-item routerLink="/employee/account/password">
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
export class EmployeeLayoutComponent implements OnInit {
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
