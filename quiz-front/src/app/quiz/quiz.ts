import { Component, computed, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizService, Reponse, Quiz, Question } from '../services/quiz.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz.html',
  styleUrls: ['./quiz.scss']
})
export class QuizComponent {

  private quizService = inject(QuizService);
  private route = inject(ActivatedRoute);

  quizId = Number(this.route.snapshot.paramMap.get('quizId'));

  quiz = signal<Quiz | null>(null);
  currentQuestionIndex = signal(0);
  selectedReponse = signal<Reponse | null>(null);
  feedback = signal<'VRAI' | 'FAUX' | null>(null);
  score = signal(0);

  // ⏳ Signals pour le timer
  totalTime = 20; // secondes par question
  timeLeft = signal(this.totalTime);
  progressCircle = signal(100); // pour SVG stroke-dasharray
  timerColor = signal('#4CAF50');
  private timerInterval: any;

  constructor() {
    this.quizService.getQuizById(this.quizId).subscribe((data) => {
      this.quiz.set(data);
      this.startTimer(); // démarre au chargement
    });
  }

  getCurrentQuestion(): Question | null {
    const qz = this.quiz();
    if (!qz) return null;
    return qz.questions[this.currentQuestionIndex()];
  }

  selectReponse(rep: Reponse | null) {
    clearInterval(this.timerInterval); // stop timer quand réponse
    if (!rep) {
      this.feedback.set('FAUX');
      return;
    }
    this.selectedReponse.set(rep);
    this.feedback.set(rep.status);
    if (rep.status === 'VRAI') {
      this.score.update((s) => s + 5);
    }
  }

  nextQuestion() {
    this.currentQuestionIndex.update((i) => i + 1);
    this.selectedReponse.set(null);
    this.feedback.set(null);
    this.startTimer(); // redémarre timer à chaque question
  }

  isQuizFinished = computed(() => {
    const qz = this.quiz();
    return qz && this.currentQuestionIndex() >= qz.questions.length;
  });

  // ⏱ Timer circulaire
  startTimer() {
    clearInterval(this.timerInterval);
    this.timeLeft.set(this.totalTime);
    this.updateCircle();

    this.timerInterval = setInterval(() => {
      this.timeLeft.update((t) => t - 1);
      this.updateCircle();

      if (this.timeLeft() <= 0) {
        clearInterval(this.timerInterval);
        this.selectReponse(null); // Timeout -> réponse fausse
      }
    }, 1000);
  }

  updateCircle() {
    const percentage = (this.timeLeft() / this.totalTime) * 100;
    this.progressCircle.set(percentage);

    if (this.timeLeft() > this.totalTime / 2) {
      this.timerColor.set('#4CAF50'); // Vert
    } else if (this.timeLeft() > this.totalTime / 4) {
      this.timerColor.set('#FFC107'); // Orange
    } else {
      this.timerColor.set('#F44336'); // Rouge
    }
  }
}
