import {Component, inject} from '@angular/core';
import { Router,RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  template: `
    <nav class="navbar">
      <a routerLink="/home" class="brand">🏠 Accueil</a>
      <a routerLink="/profile" class="brand">👤 Profile</a>
      <a routerLink="/ranking" class="brand">🎖️ Classement</a>
      @if (auth.isAdmin()) {
        <li>
          <a routerLink="/admin" class="brand">🚦 Administration Role</a>
        </li>
      }
      <div class="spacer"></div>

      @if (auth.isAuthenticated()) {
        <button (click)="onLogout()" class="logout-btn">
          🚪 Déconnexion
        </button>
      }
    </nav>
  `,
  styleUrls: ['./navbar.scss']
})
export class NavbarComponent {
  auth = inject(AuthService);
  private router = inject(Router);

  onLogout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
