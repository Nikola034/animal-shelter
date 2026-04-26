import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserResponse, UsersListResponse, MessageResponse } from '../../dto/auth/UserResponse';
import { UserRole } from '../../dto/auth/UserRole';
import { UserStatus } from '../../dto/auth/UserStatus';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly baseUrl = `${environment.apiGatewayUrl}/users`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UsersListResponse> {
    return this.http.get<UsersListResponse>(this.baseUrl);
  }

  getUsersByStatus(status: UserStatus): Observable<UsersListResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.get<UsersListResponse>(this.baseUrl, { params });
  }

  getUserById(id: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.baseUrl}/${id}`);
  }

  getCurrentUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.baseUrl}/me`);
  }

  updateUserStatus(id: string, status: UserStatus): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.baseUrl}/${id}/status`, { status });
  }

  updateUserRole(id: string, role: UserRole): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.baseUrl}/${id}/role`, { role });
  }

  deleteUser(id: string): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.baseUrl}/${id}`);
  }

  updateMyProfile(data: { name?: string; email?: string }): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.baseUrl}/me/profile`, data);
  }

  changeMyPassword(currentPassword: string, newPassword: string): Observable<MessageResponse> {
    return this.http.put<MessageResponse>(`${this.baseUrl}/me/password`, {
      current_password: currentPassword,
      new_password: newPassword,
    });
  }

  updateUserProfile(id: string, data: { name?: string; email?: string }): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.baseUrl}/${id}/profile`, data);
  }
}
