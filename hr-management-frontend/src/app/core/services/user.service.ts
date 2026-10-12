import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly API = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getAll(keyword?: string, page = 0, size = 10): Observable<ApiResponse<PageResponse<User>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (keyword) params = params.set('keyword', keyword);
    return this.http.get<ApiResponse<PageResponse<User>>>(this.API, { params });
  }

  getById(id: number): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.API}/${id}`);
  }

  lock(id: number): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.API}/${id}/lock`, {});
  }

  unlock(id: number): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.API}/${id}/unlock`, {});
  }

  changeRole(id: number, role: string): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.API}/${id}/role?role=${role}`, {});
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/${id}`);
  }
}
