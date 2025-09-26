import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {environment} from '../../environements/environement';

export interface UserQuizScore {
  quizId: number;
  username: string;
  score: number;
  quizTitle: string;
}

@Injectable({ providedIn: 'root' })
export class UserProfileService {
  private http = inject(HttpClient);
  private scoreUrl = `${environment.gatewayUrl}${environment.rewardApi}`;

  getBestUserScores(userId: number | null): Observable<UserQuizScore[]> {
    if (userId == null) return of([]);
    return this.http.get<UserQuizScore[]>(`${this.scoreUrl}/score/user/${userId}/best`);
  }


}
