import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { WorkAssignment, WorkAssignmentPage, WorkShift } from '../models/work-shift.model';

@Injectable({providedIn: 'root'})
export class WorkShiftService {
  private readonly shifts = 'http://localhost:8080/api/work-shifts';
  private readonly assignments = 'http://localhost:8080/api/work-assignments';
  constructor(private http: HttpClient) {}
  getShifts(): Observable<ApiResponse<WorkShift[]>> { return this.http.get<ApiResponse<WorkShift[]>>(this.shifts); }
  createShift(data: Partial<WorkShift>) { return this.http.post<ApiResponse<WorkShift>>(this.shifts, data); }
  updateShift(id: number, data: Partial<WorkShift>) { return this.http.put<ApiResponse<WorkShift>>(`${this.shifts}/${id}`, data); }
  deleteShift(id: number) { return this.http.delete<ApiResponse<void>>(`${this.shifts}/${id}`); }
  getAssignments(params: Record<string, unknown> = {}, mine = false): Observable<ApiResponse<WorkAssignmentPage>> {
    let hp = new HttpParams();
    Object.entries(params).forEach(([key, value]) => { if (value !== null && value !== undefined && value !== '') hp = hp.set(key, String(value)); });
    return this.http.get<ApiResponse<WorkAssignmentPage>>(`${mine ? this.assignments + '/my' : this.assignments}`, {params: hp});
  }
  createAssignment(data: Partial<WorkAssignment>) { return this.http.post<ApiResponse<WorkAssignment>>(this.assignments, data); }
  updateAssignment(id: number, data: Partial<WorkAssignment>) { return this.http.put<ApiResponse<WorkAssignment>>(`${this.assignments}/${id}`, data); }
  deleteAssignment(id: number) { return this.http.delete<ApiResponse<void>>(`${this.assignments}/${id}`); }
}
