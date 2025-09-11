import { inject, Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environements/environement';
import {Status} from '../models/status.model';

export interface UserQuizScore {
  quizId: number;
  username: string;
  score: number;
  quizTitle: string;
}

@Injectable({ providedIn: 'root' })
export class RewardService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.gatewayUrl}${environment.rewardApi}`;


  /** Démarre une nouvelle tentative */
  createAttempt(userId: number, quizId: number): Observable<UserQuizScore> {
    return this.http.post<UserQuizScore>(
      `${this.baseUrl}/new?userId=${userId}&quizId=${quizId}`,
      {}
    );
  }

  finalizeQuiz(userId: number, quizId: number, userAnswers: Status[]): Observable<UserQuizScore> {
    return this.http.post<UserQuizScore>(
      `${this.baseUrl}/finalize?userId=${userId}&quizId=${quizId}`,
      userAnswers
    );
  }

  /** Top 10 pour un quiz */
  getRanking(quizId: number): Observable<UserQuizScore[]> {
    return this.http.get<UserQuizScore[]>(`${this.baseUrl}/ranking`, {
      params: { quizId }
    });
  }

}
