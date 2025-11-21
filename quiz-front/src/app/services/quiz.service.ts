import { HttpClient } from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environements/environement';



export interface Reponse {
  id: number;
  reponse: string;
  status: 'VRAI'| 'FAUX';
}

export interface Question {
  id: number;
  question: string;
  reponses: Reponse[];
}

export interface Quiz {
  id: number;
  title: string;
  type: string;
  questions: Question[];
}

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.quizApi;

  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${environment.quizApi}/${id}`);
  }

  getQuizzesByType(type: string): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${environment.quizApi}/type/${type}`);
  }

  getTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.quizApi}/quiz/types`);
  }

}
