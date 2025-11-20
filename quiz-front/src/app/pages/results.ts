import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RewardService } from '../services/reward.service';
import {CommonModule} from '@angular/common';
import {AuthService} from '../services/auth.service';

@Component({
  selector: 'app-results',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './results.html',
  styleUrls: ['./results.scss']
})
export class ResultsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private rewardService = inject(RewardService);
  private router = inject(Router);
  private authService= inject(AuthService);

  quizId!: number;
  score: number | null = null;
  totalQuestions: number | null = null;

  ngOnInit(): void {
    this.quizId = Number(this.route.snapshot.paramMap.get('quizId'));

    const isLoggedIn = this.authService.getUserId() !== null;
    if (!isLoggedIn) {
      console.error("Utilisateur non connecté !");
      this.router.navigate(['/auth/login']);
      return;
    }

    this.rewardService.getLastScore(this.quizId).subscribe({
      next: (result) => {
        // assignations des 2 propriétés
        this.score = result.score;
        this.totalQuestions = result.totalQuestions;

        // Supprime la valeur temporaire
      },
      error: (err) => console.error("Erreur récupération score", err)
    });
  }


  goHome() {
    this.router.navigate(['/home']);
  }
}
