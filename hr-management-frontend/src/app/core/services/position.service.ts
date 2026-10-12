import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { Position, PositionRequest } from '../models/position.model';

@Injectable({ providedIn: 'root' })
export class PositionService {
  private readonly API = 'http://localhost:8080/api/positions';

  constructor(private http: HttpClient) {}

  getAll(keyword?: string): Observable<ApiResponse<Position[]>> {
    let params = new HttpParams();
    if (keyword) params = params.set('keyword', keyword);
    return this.http.get<ApiResponse<Position[]>>(this.API, { params });
  }

  getById(id: number): Observable<ApiResponse<Position>> {
    return this.http.get<ApiResponse<Position>>(`${this.API}/${id}`);
  }

  create(data: PositionRequest): Observable<ApiResponse<Position>> {
    return this.http.post<ApiResponse<Position>>(this.API, data);
  }

  update(id: number, data: PositionRequest): Observable<ApiResponse<Position>> {
    return this.http.put<ApiResponse<Position>>(`${this.API}/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API}/${id}`);
  }
}
