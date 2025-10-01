import { inject, Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environements/environement';
import {Status} from '../models/status.model';
import {ReponseTempsDto} from '../models/reponse-temps.model';

export interface UserQuizScore {
  quizId: number;
  username: string;
  score: number;
  quizTitle: string;
}

export interface QuizRanking {
  quizId: number;
  quizTitle: string;
  ranking: UserQuizScore[];
  myScore?: number;
}

@Injectable({ providedIn: 'root' })
export class RewardService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.gatewayUrl}${environment.rewardApi}`;


  /** Démarre une nouvelle tentative */
  createAttempt(userId: number, quizId: number): Observable<UserQuizScore> {
    return this.http.post<UserQuizScore>(
      `${this.baseUrl}/score/new?userId=${userId}&quizId=${quizId}`,
      {}
    );
  }

  finalizeQuiz(userId: number, quizId: number, userAnswers: ReponseTempsDto[]): Observable<UserQuizScore> {
    return this.http.post<UserQuizScore>(
      `${this.baseUrl}/score/finalize?userId=${userId}&quizId=${quizId}`,
      userAnswers
    );
  }

  getLastScore(userId: number, quizId: number): Observable<{score: number}> {
    return this.http.get<{score: number}>(`${this.baseUrl}/score/last/${userId}/${quizId}`);
  }

  /** Top 10 pour un quiz */
  getRanking(quizId: number): Observable<UserQuizScore[]> {
    return this.http.get<UserQuizScore[]>(`${this.baseUrl}/score/ranking`, {
      params: { quizId }
    });
  }

  getAllRankings(): Observable<QuizRanking[]> {
    return this.http.get<QuizRanking[]>(`${this.baseUrl}/score/ranking/all`);
  }

}
