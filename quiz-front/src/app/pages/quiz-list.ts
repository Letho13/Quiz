import {Component, inject, signal} from '@angular/core';
import {ActivatedRoute, RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {QuizService, Quiz} from '../services/quiz.service';

@Component({
  selector: 'app-quiz-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: 'quiz-list.html',
  styleUrls: ['quiz-list.scss']

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



