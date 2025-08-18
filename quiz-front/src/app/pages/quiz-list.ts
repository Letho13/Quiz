import {Component, inject, signal} from '@angular/core';
import {ActivatedRoute, RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {QuizService, Quiz} from '../services/quiz.service';

@Component({
  selector: 'app-quiz-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <h2>Quiz pour le type : {{ type }}</h2>
    <ul>
      @for (quiz of quizzes(); track quiz.id) {
        <li>
          <a [routerLink]="['/quiz', quiz.id]">{{ quiz.title }}</a>
        </li>
      }
    </ul>
  `
})
export class QuizListComponent {
  private route = inject(ActivatedRoute);
  private quizService = inject(QuizService);

  type = this.route.snapshot.paramMap.get('type')!;
  quizzes = signal<Quiz[]>([]);

  constructor() {
    if (this.type) {
      this.quizService.getQuizzesByType(this.type).subscribe({
        next: (data) => this.quizzes.set(data),
        error: (err) => console.error('Erreur chargement quiz:', err)
      });
    }
  }
}



