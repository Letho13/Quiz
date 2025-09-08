import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService, LoginRequest } from '../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule],
  template: `
    <form (ngSubmit)="onLogin()">
      <input [(ngModel)]="username" name="username" placeholder="Username" required />
      <input [(ngModel)]="password" name="password" type="password" placeholder="Password" required />
      <button type="submit">Login</button>
    </form>
  `
})
export class LoginComponent {
  username = '';
  password = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    const request: LoginRequest = { username: this.username, password: this.password };
    this.authService.login(request).subscribe({
      next: (res) => {
        localStorage.setItem('authToken', res.token); // Stockage du token
        this.router.navigate(['/home']); // Redirection
      },
      error: () => alert('Login failed')
    });
  }
}
