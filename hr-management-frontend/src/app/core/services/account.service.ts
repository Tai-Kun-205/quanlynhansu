import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../models/api-response.model';

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly api = 'http://localhost:8080/api/account';
  constructor(private http: HttpClient) {}
  changePassword(request: ChangePasswordRequest) {
    return this.http.put<ApiResponse<void>>(`${this.api}/password`, request);
  }
}
