import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {environment} from '../../environements/environement';
import {tap} from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}
export interface JwtResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {

  private http = inject(HttpClient);

  login(credentials: LoginRequest) {
    return this.http.post<JwtResponse>(
      `${environment.gatewayUrl}${environment.userApi}/login`,
      credentials
    ).pipe(
      tap((res: JwtResponse) => {
        // sauvegarde le token dans localStorage
        localStorage.setItem('jwt', res.token);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('jwt');
  }

  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
