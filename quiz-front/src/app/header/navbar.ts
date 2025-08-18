import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  template: `
    <nav class="navbar">
      <a routerLink="/" class="brand">🏠 Accueil</a>
    </nav>
  `,
  styleUrls: ['./navbar.scss']
})
export class NavbarComponent {}
