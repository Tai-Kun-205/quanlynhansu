import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { WorkShiftService } from '../../core/services/work-shift.service';
import { WorkAssignment } from '../../core/models/work-shift.model';

@Component({
  selector: 'app-my-work-shifts', standalone: true, changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, MatIconModule, MatInputModule, MatFormFieldModule, MatProgressSpinnerModule, MatTableModule],
  template: `
    <div class="page-header data-page-header"><div class="page-title-group"><span class="page-eyebrow">LỊCH CÁ NHÂN</span><h1>Lịch làm việc</h1><p>Theo dõi các ca làm việc đã được phân cho bạn.</p></div></div>
    <div class="data-toolbar"><mat-form-field appearance="outline"><mat-label>Từ ngày</mat-label><input matInput type="date" [(ngModel)]="fromDate" (change)="load()"></mat-form-field><mat-form-field appearance="outline"><mat-label>Đến ngày</mat-label><input matInput type="date" [(ngModel)]="toDate" (change)="load()"></mat-form-field></div>
    <div class="enterprise-table-card" style="padding:20px">@if(loading){<div class="table-empty"><mat-spinner diameter="36"></mat-spinner></div>}@else if(!items.length){<div class="table-empty"><mat-icon>event_busy</mat-icon>Chưa có lịch làm việc</div>}@else{<div class="table-container"><table mat-table [dataSource]="items"><ng-container matColumnDef="date"><th mat-header-cell *matHeaderCellDef>Ngày</th><td mat-cell *matCellDef="let a"><b>{{a.workDate}}</b></td></ng-container><ng-container matColumnDef="shift"><th mat-header-cell *matHeaderCellDef>Ca làm việc</th><td mat-cell *matCellDef="let a">{{a.shiftName}} ({{a.startTime}} - {{a.endTime}})</td></ng-container><ng-container matColumnDef="note"><th mat-header-cell *matHeaderCellDef>Ghi chú</th><td mat-cell *matCellDef="let a">{{a.note||'—'}}</td></ng-container><tr mat-header-row *matHeaderRowDef="cols"></tr><tr mat-row *matRowDef="let row;columns:cols"></tr></table></div>}`,
})
export class MyWorkShiftsComponent implements OnInit {
  items:WorkAssignment[]=[]; loading=false; fromDate=''; toDate=''; cols=['date','shift','note'];
  constructor(private svc:WorkShiftService,private cdr:ChangeDetectorRef){}
  ngOnInit(){this.load()}
  load(){this.loading=true;this.svc.getAssignments({fromDate:this.fromDate,toDate:this.toDate,size:100},true).subscribe({next:r=>{this.items=r.data.content;this.loading=false;this.cdr.markForCheck()},error:()=>{this.loading=false;this.cdr.markForCheck()}})}
}
