import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { LeaveRequest, LeaveRequestCreate } from '../models/leave-request.model';

@Injectable({ providedIn: 'root' })
export class LeaveRequestService {
  private readonly API = 'http://localhost:8080/api/leave-requests';

  constructor(private http: HttpClient) {}

  getAll(params?: any): Observable<ApiResponse<PageResponse<LeaveRequest>>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(k => {
        if (params[k] !== null && params[k] !== undefined && params[k] !== '') {
          httpParams = httpParams.set(k, params[k]);
        }
      });
    }
    return this.http.get<ApiResponse<PageResponse<LeaveRequest>>>(this.API, { params: httpParams });
  }

  getById(id: number): Observable<ApiResponse<LeaveRequest>> {
    return this.http.get<ApiResponse<LeaveRequest>>(`${this.API}/${id}`);
  }

  create(data: LeaveRequestCreate): Observable<ApiResponse<LeaveRequest>> {
    return this.http.post<ApiResponse<LeaveRequest>>(this.API, data);
  }

  approve(id: number): Observable<ApiResponse<LeaveRequest>> {
    return this.http.put<ApiResponse<LeaveRequest>>(`${this.API}/${id}/approve`, {});
  }

  reject(id: number): Observable<ApiResponse<LeaveRequest>> {
    return this.http.put<ApiResponse<LeaveRequest>>(`${this.API}/${id}/reject`, {});
  }

  cancel(id: number): Observable<ApiResponse<LeaveRequest>> {
    return this.http.put<ApiResponse<LeaveRequest>>(`${this.API}/${id}/cancel`, {});
  }
}
