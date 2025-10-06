import { Component, computed, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizService, Reponse, Quiz, Question } from '../services/quiz.service';
import {ActivatedRoute, Router} from '@angular/router';
import {RewardService} from '../services/reward.service';
import {Status} from '../models/status.model';
import {ReponseTempsDto} from '../models/reponse-temps.model';


@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz.html',
  styleUrls: ['./quiz.scss']
})
export class QuizComponent {

  private quizService = inject(QuizService);
  private rewardService = inject(RewardService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private answers = new Map<number, ReponseTempsDto>();

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

      this.rewardService.createAttempt( this.quizId).subscribe({
        next: (attempt) => console.log('Nouvelle tentative créée', attempt),
        error: (err) => console.error('Erreur création tentative', err)
      });

    this.quizService.getQuizById(this.quizId).subscribe((data) => {
      this.quiz.set(data);
      this.startTimer(); // démarre au chargement
    });
  }

  calculatePoints(isCorrect: boolean, timeRemaining: number): number {
    if (!isCorrect) return 0;

    if (timeRemaining >= 16) return 5;
    else if (timeRemaining >= 12) return 4;
    else if (timeRemaining >= 8) return 3;
    else if (timeRemaining >= 4) return 2;
    else if (timeRemaining > 0) return 1;
    else return 0;
  }

  getCurrentQuestion(): Question | null {
    const qz = this.quiz();
    if (!qz) return null;
    return qz.questions[this.currentQuestionIndex()];
  }

  selectReponse(rep: Reponse | null) {
    clearInterval(this.timerInterval);

    const q = this.getCurrentQuestion();
    if (q) {
      const dto = {
        status: rep ? (rep.status as Status) : Status.FAUX,
        timeRemaining: this.timeLeft()
      };

      this.answers.set(q.id, dto);

      //  Score live (comme dans le back)
      this.score.update(s => s + this.calculatePoints(dto.status === Status.VRAI, dto.timeRemaining));
    }

    this.feedback.set(rep ? rep.status : Status.FAUX);
    this.selectedReponse.set(rep);
  }

  nextQuestion() {
    this.currentQuestionIndex.update((i) => i + 1);
    this.selectedReponse.set(null);
    this.feedback.set(null);
    this.startTimer(); // redémarre timer à chaque question
  }

  finalizeQuiz() {

    const userAnswers: ReponseTempsDto[] =
      this.quiz()?.questions.map(q => this.answers.get(q.id)!) || [];

    this.rewardService.finalizeQuiz(this.quizId, userAnswers).subscribe({
      next: () => {
        console.log('Quiz finalisé ✅');
        this.router.navigate(['/results', this.quizId]);
      },
      error: (err) => console.error('Erreur lors de la finalisation', err)
    });
  }

  isQuizFinished = computed(() => {
    const qz = this.quiz();
    return qz && this.currentQuestionIndex() >= qz.questions.length;
  });

  isLastQuestion = computed(() => {
    const qz = this.quiz();
    return qz ? this.currentQuestionIndex() === qz.questions.length - 1 : false;
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
