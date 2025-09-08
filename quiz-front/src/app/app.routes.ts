import { Routes } from '@angular/router';
import { QuizComponent } from './quiz/quiz';
import { HomeComponent } from './home/home';
import { QuizListComponent } from './pages/quiz-list';
import { LayoutComponent } from './layout/layout';
import { AuthGuard } from './security/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  // Route publique pour login
  {
    path: 'auth/login',
    loadComponent: () => import('./security/Login').then(m => m.LoginComponent)
  },
  // Routes protégées par AuthGuard
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'quiz/:quizId', component: QuizComponent },
      { path: 'quizzes/type/:type', component: QuizListComponent }
    ]
  },
  // Redirection par défaut si aucune route ne matche
  { path: '**', redirectTo: 'auth/login' }
];


