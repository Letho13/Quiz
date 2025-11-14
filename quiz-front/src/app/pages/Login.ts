import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router'; // Import d'ActivatedRoute
import { AuthService, LoginRequest } from '../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './Login.html',
  styleUrls: ['./Login.scss']
})
export class LoginComponent implements OnInit {
  username = '';
  password = '';
  error: string | null = null;
  showPassword = false;

  // Variable pour stocker la route de destination initiale
  returnUrl: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute // Injection d'ActivatedRoute
  ) {}

  ngOnInit() {
    // Récupère le paramètre 'returnUrl' s'il est présent dans l'URL (ajouté par AuthGuard)
    this.returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');

    // Log pour le débogage
    if (this.returnUrl) {
      console.log(`URL de retour détectée: ${this.returnUrl}`);
    }
  }

  /**
   * Inverse l'état de visibilité du mot de passe.
   */
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onLogin() {
    this.error = null;
    const request: LoginRequest = { username: this.username, password: this.password };

    this.authService.login(request).subscribe({
      next: (res) => {
        const navigateTo = this.returnUrl || '/home';
        this.router.navigateByUrl(navigateTo);
        console.log(`Connexion réussie. Redirection vers : ${navigateTo}`);
      },
      error: (err) => {
        this.error = err.message || 'Une erreur inconnue est survenue.';
        console.error('Erreur de connexion:', err);
      }
    });
  }
}
