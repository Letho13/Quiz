import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService, LoginRequest } from '../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  username = '';
  password = '';
  error: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    const request: LoginRequest = { username: this.username, password: this.password };
    this.authService.login(request).subscribe({
      next: (res) => {
        localStorage.setItem('authToken', res.token); // Stockage du token
        this.router.navigate(['/home']); // Redirection
      },
      error: () => {
        this.error = 'Nom d’utilisateur ou mot de passe incorrect';
      }
    });
  }
}
