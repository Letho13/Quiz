import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import QuizSelectionComponent from '../pages/type-selection';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, QuizSelectionComponent],
  template: `
    <h1>Bienvenue</h1>
    <h2>Choisis le thème</h2>

    <app-type-selection></app-type-selection>
  `,
  styleUrls: ['./home.scss']
})
export class HomeComponent {

}
