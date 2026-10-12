import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Employee, EmployeeRequest } from '../models/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly QUERY_API = 'http://localhost:8080/api/employees';
  private readonly COMMAND_API = 'http://localhost:8080/api/employees';

  constructor(private http: HttpClient) {}

  getAll(params?: any): Observable<ApiResponse<PageResponse<Employee>>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(k => {
        if (params[k] !== null && params[k] !== undefined && params[k] !== '') {
          httpParams = httpParams.set(k, params[k]);
        }
      });
    }
    return this.http.get<ApiResponse<PageResponse<Employee>>>(this.QUERY_API, { params: httpParams });
  }

  getById(id: number): Observable<ApiResponse<Employee>> {
    return this.http.get<ApiResponse<Employee>>(`${this.COMMAND_API}/${id}`);
  }

  create(data: EmployeeRequest): Observable<ApiResponse<Employee>> {
    return this.http.post<ApiResponse<Employee>>(this.COMMAND_API, data);
  }

  update(id: number, data: EmployeeRequest): Observable<ApiResponse<Employee>> {
    return this.http.put<ApiResponse<Employee>>(`${this.COMMAND_API}/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.COMMAND_API}/${id}`);
  }

  updateProfile(id: number, data: Partial<EmployeeRequest>): Observable<ApiResponse<Employee>> {
    return this.http.put<ApiResponse<Employee>>(`${this.COMMAND_API}/${id}/profile`, data);
  }
}
