import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { ActivityLog } from '../models/activity-log.model';
@Injectable({providedIn:'root'})
export class ActivityLogService {
  private readonly api='http://localhost:8080/api/activity-logs';
  constructor(private http:HttpClient){}
  search(params:Record<string, unknown>={}):Observable<ApiResponse<PageResponse<ActivityLog>>> {
    let p=new HttpParams();
    Object.entries(params).forEach(([k,v])=>{if(v!==null&&v!==undefined&&v!=='')p=p.set(k,String(v));});
    return this.http.get<ApiResponse<PageResponse<ActivityLog>>>(this.api,{params:p});
  }
}
