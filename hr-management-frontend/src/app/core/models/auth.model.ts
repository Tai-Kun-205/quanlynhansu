export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  username: string;
  role: string;
  userId: number;
  employeeId: number;
}

export interface CurrentUser {
  token: string;
  username: string;
  role: string;
  userId: number;
  employeeId: number;
}
