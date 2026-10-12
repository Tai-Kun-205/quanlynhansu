import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { WorkShiftService } from '../../core/services/work-shift.service';
import { EmployeeService } from '../../core/services/employee.service';
import { WorkAssignment, WorkShift } from '../../core/models/work-shift.model';
import { Employee } from '../../core/models/employee.model';

@Component({
  selector: 'app-work-shift-list', standalone: true, changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule,
    MatSelectModule, MatSnackBarModule, MatProgressSpinnerModule, MatPaginatorModule, MatTableModule],
  template: `
  <div class="page-header data-page-header"><div class="page-title-group"><span class="page-eyebrow">VẬN HÀNH</span><h1>Ca làm việc & lịch phân ca</h1><p>Thiết lập ca và phân lịch làm việc theo từng ngày.</p></div>
    <button mat-raised-button color="primary" (click)="newShift()"><mat-icon>add</mat-icon> Thêm ca</button></div>
  <div class="enterprise-table-card" style="padding:20px;margin-bottom:20px">
    <h3>Danh sách ca làm việc</h3>
    <div class="table-container"><table mat-table [dataSource]="shifts">
      <ng-container matColumnDef="code"><th mat-header-cell *matHeaderCellDef>Mã ca</th><td mat-cell *matCellDef="let s"><span class="table-code">{{s.code}}</span></td></ng-container>
      <ng-container matColumnDef="name"><th mat-header-cell *matHeaderCellDef>Tên ca</th><td mat-cell *matCellDef="let s">{{s.name}}</td></ng-container>
      <ng-container matColumnDef="time"><th mat-header-cell *matHeaderCellDef>Khung giờ</th><td mat-cell *matCellDef="let s">{{s.startTime}} - {{s.endTime}}</td></ng-container>
      <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Trạng thái</th><td mat-cell *matCellDef="let s"><span class="badge" [class.badge-working]="s.active" [class.badge-resigned]="!s.active">{{s.active?'Đang sử dụng':'Ngừng sử dụng'}}</span></td></ng-container>
      <ng-container matColumnDef="actions"><th mat-header-cell *matHeaderCellDef>Thao tác</th><td mat-cell *matCellDef="let s"><button mat-icon-button (click)="editShift(s)"><mat-icon>edit</mat-icon></button><button mat-icon-button color="warn" (click)="removeShift(s)"><mat-icon>delete</mat-icon></button></td></ng-container>
      <tr mat-header-row *matHeaderRowDef="shiftCols"></tr><tr mat-row *matRowDef="let row;columns:shiftCols"></tr>
    </table></div>
    @if (!shifts.length && !loading) { <div class="table-empty"><mat-icon>schedule</mat-icon>Chưa có ca làm việc</div> }
  </div>
  <div class="enterprise-table-card" style="padding:20px">
    <div class="data-toolbar"><h3 style="margin:0">Lịch phân ca</h3><span class="table-summary">{{total}} lịch</span><span class="spacer"></span><button mat-raised-button color="primary" (click)="newAssignment()"><mat-icon>event</mat-icon> Phân ca</button></div>
    <div class="data-toolbar"><mat-form-field appearance="outline"><mat-label>Từ ngày</mat-label><input matInput type="date" [(ngModel)]="fromDate" (change)="loadAssignments()"></mat-form-field><mat-form-field appearance="outline"><mat-label>Đến ngày</mat-label><input matInput type="date" [(ngModel)]="toDate" (change)="loadAssignments()"></mat-form-field></div>
    @if (loading) { <div class="table-empty"><mat-spinner diameter="36"></mat-spinner></div> } @else {
    <div class="table-container"><table mat-table [dataSource]="assignments">
      <ng-container matColumnDef="employee"><th mat-header-cell *matHeaderCellDef>Nhân viên</th><td mat-cell *matCellDef="let a"><b>{{a.employeeName}}</b><small>{{a.employeeCode}}</small></td></ng-container>
      <ng-container matColumnDef="date"><th mat-header-cell *matHeaderCellDef>Ngày</th><td mat-cell *matCellDef="let a">{{a.workDate}}</td></ng-container>
      <ng-container matColumnDef="shift"><th mat-header-cell *matHeaderCellDef>Ca</th><td mat-cell *matCellDef="let a">{{a.shiftName}} ({{a.startTime}} - {{a.endTime}})</td></ng-container>
      <ng-container matColumnDef="actions"><th mat-header-cell *matHeaderCellDef>Thao tác</th><td mat-cell *matCellDef="let a"><button mat-icon-button color="warn" (click)="removeAssignment(a)"><mat-icon>delete</mat-icon></button></td></ng-container>
      <tr mat-header-row *matHeaderRowDef="assignmentCols"></tr><tr mat-row *matRowDef="let row;columns:assignmentCols"></tr>
    </table></div><mat-paginator [length]="total" [pageSize]="pageSize" [pageSizeOptions]="[10,20,50]" (page)="pageChange($event)"></mat-paginator>
    @if (!assignments.length) { <div class="table-empty"><mat-icon>event_busy</mat-icon>Chưa có lịch phân ca</div> }} 
  </div>
  @if (shiftForm) { <div class="dialog-overlay" (click)="shiftForm=false"><div class="dialog-card" (click)="$event.stopPropagation()"><h2>{{editingShift?'Chỉnh sửa ca':'Thêm ca làm việc'}}</h2><div class="dialog-form-body"><div class="dialog-form-grid"><mat-form-field appearance="outline"><mat-label>Mã ca</mat-label><input matInput [(ngModel)]="draft.code"></mat-form-field><mat-form-field appearance="outline"><mat-label>Tên ca</mat-label><input matInput [(ngModel)]="draft.name"></mat-form-field><mat-form-field appearance="outline"><mat-label>Giờ bắt đầu</mat-label><input matInput type="time" [(ngModel)]="draft.startTime"></mat-form-field><mat-form-field appearance="outline"><mat-label>Giờ kết thúc</mat-label><input matInput type="time" [(ngModel)]="draft.endTime"></mat-form-field><mat-form-field appearance="outline" class="form-field-full"><mat-label>Mô tả</mat-label><textarea matInput rows="3" [(ngModel)]="draft.description"></textarea></mat-form-field></div></div><div class="dialog-actions"><button mat-button (click)="shiftForm=false">Hủy</button><button mat-raised-button color="primary" (click)="saveShift()">Lưu</button></div></div></div> }
  @if (assignmentForm) { <div class="dialog-overlay" (click)="assignmentForm=false"><div class="dialog-card" (click)="$event.stopPropagation()"><h2>Phân ca cho nhân viên</h2><div class="dialog-form-body"><div class="dialog-form-grid"><mat-form-field appearance="outline"><mat-label>Nhân viên</mat-label><mat-select [(ngModel)]="draftAssignment.employeeId">@for(e of employees;track e.id){<mat-option [value]="e.id">{{e.code}} - {{e.fullName}}</mat-option>}</mat-select></mat-form-field><mat-form-field appearance="outline"><mat-label>Ca làm việc</mat-label><mat-select [(ngModel)]="draftAssignment.shiftId">@for(s of shifts;track s.id){<mat-option [value]="s.id">{{s.name}} ({{s.startTime}} - {{s.endTime}})</mat-option>}</mat-select></mat-form-field><mat-form-field appearance="outline" class="form-field-full"><mat-label>Ngày làm việc</mat-label><input matInput type="date" [(ngModel)]="draftAssignment.workDate"></mat-form-field></div></div><div class="dialog-actions"><button mat-button (click)="assignmentForm=false">Hủy</button><button mat-raised-button color="primary" (click)="saveAssignment()">Lưu</button></div></div></div> }`,
})
export class WorkShiftListComponent implements OnInit {
  shifts: WorkShift[]=[]; assignments: WorkAssignment[]=[]; employees: Employee[]=[]; loading=false; total=0; page=0; pageSize=10; fromDate=''; toDate=''; shiftForm=false; assignmentForm=false; editingShift: WorkShift|null=null; draft: any={}; draftAssignment:any={}; shiftCols=['code','name','time','status','actions']; assignmentCols=['employee','date','shift','actions'];
  constructor(private svc:WorkShiftService,private empSvc:EmployeeService,private snack:MatSnackBar,private cdr:ChangeDetectorRef){}
  ngOnInit(){this.loadShifts();this.empSvc.getAll({size:1000}).subscribe(r=>{this.employees=r.data.content;this.cdr.markForCheck()});this.loadAssignments()}
  loadShifts(){this.svc.getShifts().subscribe(r=>{this.shifts=r.data;this.cdr.markForCheck()})}
  loadAssignments(){this.loading=true;this.svc.getAssignments({fromDate:this.fromDate,toDate:this.toDate,page:this.page,size:this.pageSize}).subscribe({next:r=>{this.assignments=r.data.content;this.total=r.data.totalElements;this.loading=false;this.cdr.markForCheck()},error:()=>{this.loading=false;this.snack.open('Không thể tải lịch làm việc','Đóng',{duration:3000});this.cdr.markForCheck()}})}
  newShift(){this.editingShift=null;this.draft={active:true};this.shiftForm=true} editShift(s:WorkShift){this.editingShift=s;this.draft={...s};this.shiftForm=true}
  saveShift(){const o=this.editingShift?this.svc.updateShift(this.editingShift.id,this.draft):this.svc.createShift(this.draft);o.subscribe({next:()=>{this.shiftForm=false;this.loadShifts();this.snack.open('Đã lưu ca làm việc','Đóng',{duration:2500})},error:e=>this.snack.open(e.error?.message||'Không thể lưu ca','Đóng',{duration:3000})})}
  removeShift(s:WorkShift){if(confirm('Ngừng sử dụng ca này?'))this.svc.deleteShift(s.id).subscribe(()=>this.loadShifts())}
  newAssignment(){this.draftAssignment={workDate:new Date().toISOString().slice(0,10)};this.assignmentForm=true}
  saveAssignment(){this.svc.createAssignment(this.draftAssignment).subscribe({next:()=>{this.assignmentForm=false;this.loadAssignments();this.snack.open('Phân ca thành công','Đóng',{duration:2500})},error:e=>this.snack.open(e.error?.message||'Không thể phân ca','Đóng',{duration:3000})})}
  removeAssignment(a:WorkAssignment){if(confirm('Xóa lịch phân ca này?'))this.svc.deleteAssignment(a.id).subscribe(()=>this.loadAssignments())}
  pageChange(e:PageEvent){this.page=e.pageIndex;this.pageSize=e.pageSize;this.loadAssignments()}
}
