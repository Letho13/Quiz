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

export interface QuizScoreResult {
  score: number;
  totalQuestions: number; // Ajout du nombre total de questions
}

@Injectable({ providedIn: 'root' })
export class RewardService {
  private http = inject(HttpClient);
  private baseUrl = environment.rewardApi;


  /** Démarre une nouvelle tentative */
  createAttempt(quizId: number): Observable<UserQuizScore> {
    return this.http.post<UserQuizScore>(
      `${this.baseUrl}/new?quizId=${quizId}`, {}
    );
  }

  finalizeQuiz(quizId: number, userAnswers: ReponseTempsDto[]): Observable<UserQuizScore> {
    return this.http.post<UserQuizScore>(
      `${this.baseUrl}/finalize?quizId=${quizId}`,
      userAnswers
    );
  }

  getLastScore(quizId: number): Observable<QuizScoreResult> {
    return this.http.get<QuizScoreResult>(`${this.baseUrl}/last/${quizId}`);
  }


  /** Top 10 pour un quiz */
  getRanking(quizId: number): Observable<UserQuizScore[]> {
    return this.http.get<UserQuizScore[]>(`${this.baseUrl}/ranking`, {
      params: { quizId }
    });
  }

  getAllRankings(): Observable<QuizRanking[]> {
    return this.http.get<QuizRanking[]>(`${this.baseUrl}/ranking/all`);
  }

}
