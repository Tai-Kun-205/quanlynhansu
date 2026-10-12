import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { Department, DepartmentRequest } from '../models/department.model';

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private readonly API = 'http://localhost:8080/api/departments';

  constructor(private http: HttpClient) {}

  getAll(keyword?: string): Observable<ApiResponse<Department[]>> {
    let params = new HttpParams();
    if (keyword) params = params.set('keyword', keyword);
    return this.http.get<ApiResponse<Department[]>>(this.API, { params });
  }

  getById(id: number): Observable<ApiResponse<Department>> {
    return this.http.get<ApiResponse<Department>>(`${this.API}/${id}`);
  }

  create(data: DepartmentRequest): Observable<ApiResponse<Department>> {
    return this.http.post<ApiResponse<Department>>(this.API, data);
  }

  update(id: number, data: DepartmentRequest): Observable<ApiResponse<Department>> {
    return this.http.put<ApiResponse<Department>>(`${this.API}/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/${id}`);
  }
}
