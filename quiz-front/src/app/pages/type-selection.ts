import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import { RouterModule} from '@angular/router';
import { QuizService} from '../services/quiz.service';


@Component({
  selector: 'app-type-selection',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './type-selection.html',
  styleUrls: ['./type-selection.scss']
})

export default class QuizSelectionComponent {
  private quizService = inject(QuizService);
  quizTypes = signal<string[]>([]);

  constructor() {
    this.quizService.getTypes().subscribe({
      next: (types) => this.quizTypes.set(types),
      error: (err) => console.error('Erreur chargement des types :', err),
    });
  }
}
