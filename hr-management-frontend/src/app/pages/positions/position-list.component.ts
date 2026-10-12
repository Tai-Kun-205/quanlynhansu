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
import { PositionService } from '../../core/services/position.service';
import { Position, POS_STATUS_MAP } from '../../core/models/position.model';

@Component({
  selector: 'app-position-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule,
    MatProgressSpinnerModule, MatTooltipModule, MatCardModule],
  template: `
    <div class="page-header data-page-header">
      <div class="page-title-group"><span class="page-eyebrow">TỔ CHỨC</span><h1>Quản lý chức vụ</h1><p>Quản lý danh mục chức danh và vai trò trong doanh nghiệp.</p></div>
      <div class="page-header-actions"><button mat-raised-button color="primary" (click)="openForm()"><mat-icon>add</mat-icon> Thêm chức vụ</button></div>
    </div>

    <div class="data-toolbar">
      <mat-form-field appearance="outline" class="toolbar-search">
        <mat-label>Tìm kiếm</mat-label>
        <input matInput [(ngModel)]="keyword" placeholder="Tên, mã chức vụ..." (keyup.enter)="search()">
        <button matSuffix mat-icon-button (click)="search()"><mat-icon>search</mat-icon></button>
      </mat-form-field>
      <span class="table-summary"><mat-icon>badge</mat-icon>{{ positions.length }} chức vụ</span>
    </div>

    <div class="table-wrapper enterprise-table-card">
      @if (loading) {
        <div class="loading-overlay"><mat-spinner diameter="40"></mat-spinner></div>
      } @else {
        <div class="table-container">
          <table mat-table [dataSource]="positions">
            <ng-container matColumnDef="code">
              <th mat-header-cell *matHeaderCellDef style="width:120px">Mã CV</th>
              <td mat-cell *matCellDef="let p"><span class="table-code">{{ p.code }}</span></td>
            </ng-container>
            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Tên Chức Vụ</th>
              <td mat-cell *matCellDef="let p"><div class="table-entity"><mat-icon class="table-entity-icon">badge</mat-icon><span class="table-primary">{{ p.name }}</span></div></td>
            </ng-container>
            <ng-container matColumnDef="description">
              <th mat-header-cell *matHeaderCellDef>Mô Tả</th>
              <td mat-cell *matCellDef="let p"><span class="table-muted">{{ p.description || '—' }}</span></td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef style="width:140px">Trạng Thái</th>
              <td mat-cell *matCellDef="let p">
                <span [class]="p.status==='ACTIVE'?'badge badge-active':'badge badge-inactive'">{{ statusMap[p.status] || p.status }}</span>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef style="width:100px">Thao Tác</th>
              <td mat-cell *matCellDef="let p">
                <div class="table-actions">
                  <button mat-icon-button color="primary" (click)="openForm(p)" matTooltip="Chỉnh sửa"><mat-icon>edit</mat-icon></button>
                  <button mat-icon-button color="warn" (click)="delete(p)" matTooltip="Xóa"><mat-icon>delete</mat-icon></button>
                </div>
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="cols"></tr>
            <tr mat-row *matRowDef="let row; columns: cols"></tr>
          </table>
        </div>
        @if (!positions.length) {
          <div class="table-empty"><mat-icon>inbox</mat-icon> Chưa có chức vụ nào</div>
        }
      }
    </div>

    @if (showForm) {
      <div class="dialog-overlay" (click)="closeForm()">
        <mat-card class="dialog-card" (click)="$event.stopPropagation()">
          <mat-card-header><mat-card-title>{{ editItem ? 'Chỉnh Sửa Chức Vụ' : 'Thêm Chức Vụ' }}</mat-card-title></mat-card-header>
          <mat-card-content style="padding:20px 16px 0">
            <form [formGroup]="frm">
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Mã Chức Vụ *</mat-label>
                <input matInput formControlName="code" [readonly]="!!editItem">
                @if (frm.get('code')?.touched && frm.get('code')?.hasError('required')) { <mat-error>Bắt buộc</mat-error> }
              </mat-form-field>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Tên Chức Vụ *</mat-label>
                <input matInput formControlName="name">
                @if (frm.get('name')?.touched && frm.get('name')?.hasError('required')) { <mat-error>Bắt buộc</mat-error> }
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
            <button mat-raised-button color="primary" (click)="save()" [disabled]="frm.invalid || saving">{{ saving ? 'Đang lưu...' : 'Lưu' }}</button>
          </mat-card-actions>
        </mat-card>
      </div>
    }
  `
})
export class PositionListComponent implements OnInit {
  positions: Position[] = [];
  cols = ['code', 'name', 'description', 'status', 'actions'];
  loading = false; keyword = ''; showForm = false; editItem: Position | null = null;
  frm!: FormGroup; saving = false; statusMap = POS_STATUS_MAP;

  constructor(private svc: PositionService, private fb: FormBuilder, private snack: MatSnackBar, private cdr: ChangeDetectorRef) {}

  ngOnInit() { this.initForm(); this.load(); }

  initForm(p?: Position) {
    this.frm = this.fb.group({ code: [p?.code||'', Validators.required], name: [p?.name||'', Validators.required], description: [p?.description||''], status: [p?.status||'ACTIVE'] });
  }

  load() {
    this.loading = true; this.cdr.markForCheck();
    this.svc.getAll(this.keyword || undefined).subscribe({
      next: r => { this.positions = r.data; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  search() { this.load(); }
  openForm(p?: Position) { this.editItem = p||null; this.initForm(p); this.showForm = true; this.cdr.markForCheck(); }
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

  delete(p: Position) {
    if (!confirm(`Xóa chức vụ "${p.name}"?`)) return;
    this.svc.delete(p.id).subscribe({
      next: r => { this.snack.open(r.message||'Đã xóa','Đóng',{duration:3000}); this.load(); },
      error: e => { this.snack.open(e.error?.message||'Không thể xóa','Đóng',{duration:4000}); }
    });
  }
}
