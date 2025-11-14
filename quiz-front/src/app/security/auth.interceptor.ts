import {inject} from '@angular/core';
import {HttpInterceptorFn,HttpErrorResponse} from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs';
import { throwError } from 'rxjs';


export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  // Assurez-vous que cette méthode est bien nommée .getToken() dans votre service
  const token = authService.getToken();

  const clonedReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(clonedReq).pipe(
    catchError((err: HttpErrorResponse) => {
      // Vérification que l'erreur est bien un 401
      if (err.status === 401) {
        // Log pour débogage (optionnel)
        console.warn('401 Unauthorized détecté. Déconnexion et redirection vers login.');

        //  Nettoyage du token (important pour invalider la session front-end)
        authService.logout();

        //  Redirection vers la page de connexion

        router.navigate(['auth/login']);

        //  Retourne une erreur observable
        return throwError(() => new Error('Session expirée ou non autorisée.'));
      }

      // Pour toutes les autres erreurs (400, 500, etc.), les propager
      return throwError(() => err);
    })
  );
};
