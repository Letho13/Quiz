import { Component, inject, OnInit } from '@angular/core';
import { RewardService, UserQuizScore, QuizRanking } from '../services/reward.service';
import { ActivatedRoute } from '@angular/router';
import { ChartConfiguration } from 'chart.js';

export interface EnhancedQuizRanking extends QuizRanking {
  uniquePlayerCount: number;
}

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.html',
  styleUrls: ['./ranking.scss']
})
export class RankingComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private rewardService = inject(RewardService);

  allRankings: EnhancedQuizRanking[] = [];

  ranking: UserQuizScore[] = [];
  quizTitle: string = '';
  quizId!: number;

  barChartLabels: string[] = [];
  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Score', backgroundColor: '#007bff' }
    ]
  };

  constructor() {}

  ngOnInit(): void {
    this.rewardService.getAllRankings().subscribe({
      next: (data: QuizRanking[]) => {
        // Traitement des données pour calculer le nombre de joueurs uniques
        this.allRankings = data.map(quiz => ({
          ...quiz,
          uniquePlayerCount: this.getUniquePlayerCount(quiz.ranking)
        }));
      },
      error: (err) => console.error('Erreur récupération rankings', err)
    });
  }

  /**
   * Calcule le nombre de joueurs uniques dans un classement.
   * @param scores Le tableau complet des scores (un score par tentative).
   * @returns Le nombre d'utilisateurs uniques.
   */
  private getUniquePlayerCount(scores: UserQuizScore[]): number {
    const usernames = scores.map(s => s.username);
    // Utilise un Set pour obtenir uniquement les valeurs distinctes (uniques)
    const uniqueUsernames = new Set(usernames);
    return uniqueUsernames.size;
  }


  loadRanking(): void {
    this.rewardService.getRanking(this.quizId).subscribe({
      next: (data) => {
        this.ranking = data;
        if (data.length > 0) {
          this.quizTitle = data[0].quizTitle;
        }
        // 🎨 Mettre à jour le graphique
        this.barChartLabels = data.map(s => s.username);
        this.barChartData = {
          labels: this.barChartLabels,
          datasets: [
            { data: data.map(s => s.score), label: 'Score', backgroundColor: '#28a745' }
          ]
        };
      },
      error: (err) => {
        console.error('Erreur lors du chargement du classement :', err);
      }
    });
  }
}
