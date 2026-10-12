import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./pages/layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'reports', loadComponent: () => import('./pages/reports/report.component').then(m => m.ReportComponent) },
      { path: 'employees', loadComponent: () => import('./pages/employees/employee-list.component').then(m => m.EmployeeListComponent) },
      { path: 'departments', loadComponent: () => import('./pages/departments/department-list.component').then(m => m.DepartmentListComponent) },
      { path: 'positions', loadComponent: () => import('./pages/positions/position-list.component').then(m => m.PositionListComponent) },
      { path: 'attendances', loadComponent: () => import('./pages/attendances/attendance-list.component').then(m => m.AttendanceListComponent) },
      { path: 'leave-requests', loadComponent: () => import('./pages/leave-requests/leave-request-list.component').then(m => m.LeaveRequestListComponent) },
      { path: 'users', loadComponent: () => import('./pages/users/user-list.component').then(m => m.UserListComponent) },
      { path: 'work-shifts', loadComponent: () => import('./pages/work-shifts/work-shift-list.component').then(m => m.WorkShiftListComponent) },
      { path: 'notifications', loadComponent: () => import('./pages/notifications/notification-list.component').then(m => m.NotificationListComponent) },
      { path: 'activity-logs', loadComponent: () => import('./pages/activity-logs/activity-log-list.component').then(m => m.ActivityLogListComponent) },
      { path: 'payrolls', loadComponent: () => import('./pages/payrolls/payroll-list.component').then(m => m.PayrollListComponent) },
      { path: 'account/password', loadComponent: () => import('./pages/account/change-password.component').then(m => m.ChangePasswordComponent) },
    ]
  },
  {
    path: 'employee',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/layout/employee-layout.component').then(m => m.EmployeeLayoutComponent),
    children: [
      { path: '', redirectTo: 'profile', pathMatch: 'full' },
      { path: 'profile', loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent) },
      { path: 'attendance', loadComponent: () => import('./pages/attendances/my-attendance.component').then(m => m.MyAttendanceComponent) },
      { path: 'leave-requests', loadComponent: () => import('./pages/leave-requests/my-leave-requests.component').then(m => m.MyLeaveRequestsComponent) },
      { path: 'work-shifts', loadComponent: () => import('./pages/work-shifts/my-work-shifts.component').then(m => m.MyWorkShiftsComponent) },
      { path: 'notifications', loadComponent: () => import('./pages/notifications/notification-list.component').then(m => m.NotificationListComponent) },
      { path: 'payrolls', loadComponent: () => import('./pages/payrolls/payroll-list.component').then(m => m.PayrollListComponent) },
      { path: 'account/password', loadComponent: () => import('./pages/account/change-password.component').then(m => m.ChangePasswordComponent) },
    ]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
