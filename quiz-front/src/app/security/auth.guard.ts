import {Injectable} from '@angular/core';

import {CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {AuthService} from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(

    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    } else {
      // Déconnecte (nettoyage de sécurité)
      this.authService.logout();

      // Redirige vers la page de connexion
      // Stocke l'URL d'origine (state.url) dans le paramètre de requête 'returnUrl'
      this.router.navigate(['auth/login'], {
        queryParams: { returnUrl: state.url }
      });

      return false;
    }
  }
}
