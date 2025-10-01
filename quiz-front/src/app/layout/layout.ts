import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../header/navbar';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <main class="content">
      <router-outlet></router-outlet>

    </main>
  `
})
export class LayoutComponent {}
