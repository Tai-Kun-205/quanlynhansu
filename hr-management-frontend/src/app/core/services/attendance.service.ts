import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Attendance } from '../models/attendance.model';

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private readonly API = 'http://localhost:8080/api/attendances';

  constructor(private http: HttpClient) {}

  getAll(params?: any): Observable<ApiResponse<PageResponse<Attendance>>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(k => {
        if (params[k] !== null && params[k] !== undefined && params[k] !== '') {
          httpParams = httpParams.set(k, params[k]);
        }
      });
    }
    return this.http.get<ApiResponse<PageResponse<Attendance>>>(this.API, { params: httpParams });
  }

  getMy(params?: any): Observable<ApiResponse<PageResponse<Attendance>>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(k => {
        if (params[k] !== null && params[k] !== undefined && params[k] !== '') {
          httpParams = httpParams.set(k, params[k]);
        }
      });
    }
    return this.http.get<ApiResponse<PageResponse<Attendance>>>(`${this.API}/my`, { params: httpParams });
  }

  checkIn(): Observable<ApiResponse<Attendance>> {
    return this.http.post<ApiResponse<Attendance>>(`${this.API}/check-in`, {});
  }

  checkOut(): Observable<ApiResponse<Attendance>> {
    return this.http.post<ApiResponse<Attendance>>(`${this.API}/check-out`, {});
  }
}
