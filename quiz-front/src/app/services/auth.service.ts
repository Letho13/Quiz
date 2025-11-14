import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap, catchError, throwError, retry, timer } from 'rxjs';
import { environment } from '../../environements/environement';
import { jwtDecode } from 'jwt-decode';

// Configuration pour la nouvelle tentative (Retry)
const RETRY_CONFIG = {
  MAX_RETRIES: 3,         // Nombre maximal de tentatives
  INITIAL_DELAY_MS: 500   // Délai initial avant la première nouvelle tentative
};

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

  // Définit les chemins API relatifs pour la cohérence
  private readonly USER_API_URL = environment.userApi;
  private readonly LOGIN_API_URL = '/api/auth/login';


  register(user: { username: string; email: string; password: string }) {
    return this.http.post(
      `${this.USER_API_URL}/register`, user
    );
  }

  /**
   * Tente de connecter l'utilisateur avec une logique de RETRY
   * en cas d'erreur transitoire (ex: 503 Service Unavailable, Timeout).
   */
  login(credentials: LoginRequest): Observable<JwtResponse> {
    // Variable pour suivre la tentative dans la fonction 'delay' de retry
    let attempt = 0;

    return this.http.post<JwtResponse>(
      this.LOGIN_API_URL,
      credentials
    ).pipe(
      // --- Logique de Retry avec Backoff ---
      retry({
        count: RETRY_CONFIG.MAX_RETRIES,
        // Utilisation de timer() pour retourner un ObservableInput<any>
        delay: (error: any, retryCount: number) => {
          // retryCount est le nombre d'échecs déjà survenus (1, 2, 3...)
          attempt = retryCount;

          // Vérifie si l'erreur est de type "transitoire" (5xx ou réseau)
          const isRetryable = error instanceof HttpErrorResponse && (error.status === 0 || error.status >= 500);

          if (!isRetryable || attempt >= RETRY_CONFIG.MAX_RETRIES) {
            // Si ce n'est pas une erreur transitoire (ex: 401) ou si les tentatives sont épuisées,
            // on lève l'erreur immédiatement.
            return throwError(() => error);
          }

          // Calcul du délai exponentiel avec Jitter
          const baseDelay = RETRY_CONFIG.INITIAL_DELAY_MS * Math.pow(2, attempt - 1);
          const jitter = Math.random() * RETRY_CONFIG.INITIAL_DELAY_MS * 0.5;
          const delayTime = Math.round(baseDelay + jitter);

          console.warn(`[Login] Échec transitoire (Tentative #${attempt + 1}/${RETRY_CONFIG.MAX_RETRIES}). Nouvelle tentative dans ${delayTime}ms.`);

          return timer(delayTime);
        }
      }),

      // 2. Traitement en cas de succès
      tap((res: JwtResponse) => {
        localStorage.setItem('jwt', res.token);
        // Si attempt > 0, cela signifie que la connexion a réussi après une ou plusieurs retries
        console.log(`[Login] Connexion réussie${attempt > 0 ? ` après ${attempt} tentative(s).` : '.'}`);
      }),

      // 3. Gestion des erreurs finales (après toutes les tentatives ou 4xx)
      catchError((error: HttpErrorResponse) => {
        let customError = 'Échec de la connexion. Veuillez réessayer.';

        if (error.status === 401 || error.status === 403) {
          customError = 'Identifiants invalides ou accès refusé.';
        } else if (error.status === 0 || error.status >= 500) {
          customError = `Le service est actuellement indisponible (Erreur ${error.status}). Veuillez réessayer plus tard.`;
        }

        console.error('[Login] Échec après toutes les tentatives ou erreur non transitoire:', error);
        return throwError(() => new Error(customError));
      })
    );
  }

  // --- Le reste de vos méthodes reste inchangé ---

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

  /** Récupère les infos décodées du JWT */
  getUserId(): number | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.userId ?? null;
    } catch {
      return null;
    }
  }

  getUser(): Observable<UserInfo> {
    const userId = this.getUserId();
    if (!userId) throw new Error('User not logged in');
    return this.http.get<UserInfo>(`${this.USER_API_URL}/${userId}`);
  }

  updateUser(userUpdate: UserUpdateDto): Observable<void> {
    const userId = this.getUserId();
    if (!userId) throw new Error('User not logged in');
    userUpdate.id = userId;

    return this.http.put<void>(`${this.USER_API_URL}/${userId}`, userUpdate);
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
