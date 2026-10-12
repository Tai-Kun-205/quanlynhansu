import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ExportService {
  private readonly API = 'http://localhost:8080/api/exports';

  constructor(private http: HttpClient) {}

  employees(filters: any = {}): Observable<Blob> {
    return this.http.get(`${this.API}/employees`, { params: this.params(filters), responseType: 'blob' });
  }

  attendances(filters: any = {}): Observable<Blob> {
    return this.http.get(`${this.API}/attendances`, { params: this.params(filters), responseType: 'blob' });
  }

  leaveRequests(filters: any = {}): Observable<Blob> {
    return this.http.get(`${this.API}/leave-requests`, { params: this.params(filters), responseType: 'blob' });
  }

  payrolls(filters: any = {}): Observable<Blob> {
    return this.http.get(`${this.API}/payrolls`, { params: this.params(filters), responseType: 'blob' });
  }

  private params(filters: any) {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') params = params.set(key, String(value));
    });
    return params;
  }
}
