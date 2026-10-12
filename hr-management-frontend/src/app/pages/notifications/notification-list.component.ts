import {ChangeDetectionStrategy,ChangeDetectorRef,Component,OnInit} from '@angular/core';
import {CommonModule} from '@angular/common'; import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon'; import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input'; import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSnackBar,MatSnackBarModule} from '@angular/material/snack-bar'; import {MatCardModule} from '@angular/material/card';
import {AuthService} from '../../core/services/auth.service'; import {NotificationService} from '../../core/services/notification.service';
import {NotificationItem} from '../../core/models/notification.model';
@Component({selector:'app-notification-list',standalone:true,changeDetection:ChangeDetectionStrategy.OnPush,imports:[CommonModule,FormsModule,MatIconModule,MatButtonModule,MatInputModule,MatFormFieldModule,MatSnackBarModule,MatCardModule],template:`
<div class="page-header data-page-header"><div class="page-title-group"><span class="page-eyebrow">NỘI BỘ</span><h1>Thông báo nội bộ</h1><p>Cập nhật thông tin mới nhất từ doanh nghiệp.</p></div><div class="page-header-actions"><button mat-stroked-button (click)="markAllRead()" [disabled]="!hasUnread"><mat-icon>done_all</mat-icon>Đánh dấu tất cả đã đọc</button>@if(isAdmin){<button mat-raised-button color="primary" (click)="form=true"><mat-icon>add</mat-icon>Tạo thông báo</button>}</div></div>
@if(isAdmin&&form){<div class="dialog-overlay"><div class="dialog-card"><h2>Tạo thông báo</h2><div class="dialog-form-body"><div class="dialog-form-grid"><mat-form-field appearance="outline" class="form-field-full"><mat-label>Tiêu đề</mat-label><input matInput [(ngModel)]="draft.title"></mat-form-field><mat-form-field appearance="outline" class="form-field-full"><mat-label>Nội dung</mat-label><textarea matInput rows="5" [(ngModel)]="draft.content"></textarea></mat-form-field></div></div><div class="dialog-actions"><button mat-button (click)="form=false">Hủy</button><button mat-raised-button color="primary" (click)="save()">Gửi thông báo</button></div></div></div>}
<div class="notification-grid">@for(n of items;track n.id){<mat-card class="notification-card" [class.notification-unread]="!n.read"><mat-card-header><mat-icon mat-card-avatar>{{n.read?'mark_email_read':'notifications_active'}}</mat-icon><mat-card-title>{{n.title}}</mat-card-title><mat-card-subtitle>{{n.departmentName}} · {{n.createdAt|date:'dd/MM/yyyy HH:mm'}}</mat-card-subtitle></mat-card-header><mat-card-content><p>{{n.content}}</p></mat-card-content><mat-card-actions>@if(!n.read){<button mat-button color="primary" (click)="markRead(n)">Đánh dấu đã đọc</button>}@if(isAdmin){<button mat-button color="warn" (click)="remove(n)">Xóa</button>}</mat-card-actions></mat-card>}</div>
@if(!items.length){<div class="table-empty"><mat-icon>notifications_none</mat-icon>Chưa có thông báo</div>}`})
export class NotificationListComponent implements OnInit{
 items:NotificationItem[]=[];form=false;draft:any={};isAdmin=false;
 constructor(private svc:NotificationService,private auth:AuthService,private snack:MatSnackBar,private cdr:ChangeDetectorRef){}
 ngOnInit(){this.isAdmin=this.auth.currentUser()?.role==='ADMIN';this.load()}
 get hasUnread(){return this.items.some(n=>!n.read)}
 load(){(this.isAdmin?this.svc.all():this.svc.my()).subscribe(r=>{this.items=r.data;this.cdr.markForCheck()})}
 save(){this.svc.create(this.draft).subscribe({next:()=>{this.form=false;this.draft={};this.load();this.snack.open('Đã gửi thông báo','Đóng',{duration:2500})},error:e=>this.snack.open(e.error?.message||'Không thể tạo thông báo','Đóng',{duration:3000})})}
 markRead(n:NotificationItem){this.svc.read(n.id).subscribe(()=>{n.read=true;this.cdr.markForCheck()})}
 markAllRead(){this.svc.readAll().subscribe(()=>{this.items.forEach(n=>n.read=true);this.cdr.markForCheck();this.snack.open('Đã đánh dấu tất cả đã đọc','Đóng',{duration:2200})})}
 remove(n:NotificationItem){if(confirm('Xóa thông báo này?'))this.svc.delete(n.id).subscribe(()=>this.load())}
}
