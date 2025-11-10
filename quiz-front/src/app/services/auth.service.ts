import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {environment} from '../../environements/environement';
import {Observable, tap} from 'rxjs';
import {jwtDecode} from 'jwt-decode';


export interface LoginRequest {
  username: string;
  password: string;
}
export interface JwtResponse {
  token: string;
}

export interface DecodedToken {
  sub: string;
  userId: number;
  iat: number;
  exp: number;
  role: string;
  [key: string]: any;
}

export interface UserInfo {
  userId: number;
  email: string;
  username: string;
}

export interface UserUpdateDto {
  id?: number;
  username?: string | null;
  email?: string | null;
  password?: string | null;
}


@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private http = inject(HttpClient);

  register(user: { username: string; email: string; password: string }) {
    return this.http.post(
      `${environment.gatewayUrl}${environment.userApi}/register`, user
    );
  }

  login(credentials: LoginRequest) {
    return this.http.post<JwtResponse>(
      `${environment.gatewayUrl}${environment.userApi}/auth/login`,
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
    const token = this.getToken();
    if (!token) return false;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.exp * 1000 > Date.now(); // token non expiré
    } catch {
      return false;
    }
  }

  /**  Récupère les infos décodées du JWT */
  getUserId(): number | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.userId ?? null; // ⚡ utiliser userId au lieu de sub
    } catch {
      return null;
    }
  }

  getUser(): Observable<UserInfo> {
    const userId = this.getUserId();
    if (!userId) throw new Error('User not logged in');
    return this.http.get<UserInfo>(`${environment.gatewayUrl}${environment.userApi}/${userId}`);
  }

  updateUser(userUpdate: UserUpdateDto): Observable<void> {
    const userId = this.getUserId();
    if (!userId) throw new Error('User not logged in');
    // s'assurer que l'id dans le path et le body soit le bon
    userUpdate.id = userId;
    return this.http.put<void>(`${environment.gatewayUrl}${environment.userApi}/${userId}`, userUpdate);
  }

  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    const decoded = jwtDecode<DecodedToken>(token);
    return decoded.role || null;
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

}
