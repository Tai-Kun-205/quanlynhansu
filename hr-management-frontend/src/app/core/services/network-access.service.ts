import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { NetworkStatus } from '../models/network-status.model';

@Injectable({ providedIn: 'root' })
export class NetworkAccessService {
  private readonly API = 'http://localhost:8080/api/demo/network-status';

  constructor(private http: HttpClient) {}

  getStatus(): Observable<ApiResponse<NetworkStatus>> {
    return this.http.get<ApiResponse<NetworkStatus>>(this.API);
  }

  updateStatus(companyNetwork: boolean): Observable<ApiResponse<NetworkStatus>> {
    return this.http.put<ApiResponse<NetworkStatus>>(this.API, { companyNetwork });
  }
}
