import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {environment} from '../../environements/environement';
import {map, Observable, of, tap} from 'rxjs';
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
  [key: string]: any;
}

export interface UserInfo {
  userId: number;
  email: string;
  username: string;
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

  getUsername(): Observable<string> {
    const userId = this.getUserId();
    if (!userId) throw new Error('User not logged in');

    return this.http
      .get<UserInfo>(`${environment.gatewayUrl}/user/${userId}`)
      .pipe(map(info => info.username));
  }

  getUserEmail(): Observable<string> {
    const userId = this.getUserId();
    if (!userId) return of(''); // <- plutôt que throw
    return this.http
      .get<UserInfo>(`${environment.gatewayUrl}/user/${userId}`)
      .pipe(map(info => info.email));
  }


}
