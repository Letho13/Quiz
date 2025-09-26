import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environements/environement';
import {Observable} from 'rxjs';

export interface UserDto {
  id: number;
  username: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.gatewayUrl}${environment.userApi}/user`;

  getUsers(page: number, size: number): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  searchUsers(query: string, page: number, size: number): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.baseUrl}/search?query=${query}&page=${page}&size=${size}`);
  }

  updateUserRole(id: number, role: string): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}`, { role });
  }
}
