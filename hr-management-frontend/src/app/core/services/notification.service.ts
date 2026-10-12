import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { NotificationItem } from '../models/notification.model';
@Injectable({providedIn:'root'})
export class NotificationService {
  private api='http://localhost:8080/api/notifications';
  constructor(private http:HttpClient){}
  my():Observable<ApiResponse<NotificationItem[]>>{return this.http.get<ApiResponse<NotificationItem[]>>(this.api+'/my')}
  all():Observable<ApiResponse<NotificationItem[]>>{return this.http.get<ApiResponse<NotificationItem[]>>(this.api)}
  create(data:any){return this.http.post<ApiResponse<NotificationItem>>(this.api,data)}
  delete(id:number){return this.http.delete<ApiResponse<void>>(`${this.api}/${id}`)}
  read(id:number){return this.http.put<ApiResponse<void>>(`${this.api}/${id}/read`,{})}
  unreadCount(){return this.http.get<ApiResponse<number>>(`${this.api}/unread-count`)}
  readAll(){return this.http.put<ApiResponse<void>>(`${this.api}/read-all`,{})}
}
