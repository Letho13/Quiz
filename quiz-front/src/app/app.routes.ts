import { Routes } from '@angular/router';
import { QuizComponent } from './quiz/quiz';
import { HomeComponent } from './home/home';
import { QuizListComponent } from './pages/quiz-list';
import { LayoutComponent } from './layout/layout';
import { AuthGuard } from './security/auth.guard';
import {UserProfileComponent} from './pages/user-profile';
import {ResultsComponent} from './pages/results';
import {RankingComponent} from './ranking/ranking';
import {AdminUserListComponent} from './pages/admin-user-list';
import {AdminGuard} from './security/admin.guard';
import QuizSelectionComponent from './pages/type-selection';

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  // Route publique pour login
  {
    path: 'auth/login',
    loadComponent: () => import('./pages/Login').then(m => m.LoginComponent)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./pages/register').then(m => m.RegisterComponent)
  },
  // Routes protégées par AuthGuard
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'quiz/:quizId', component: QuizComponent },
      { path: 'quizzes/type/:type', component: QuizListComponent },
      {path: 'profile', component: UserProfileComponent},
      {path: 'results/:quizId', component: ResultsComponent},
      {path: 'themes', component: QuizSelectionComponent},
      { path: 'admin', component: AdminUserListComponent, canActivate: [AdminGuard]  },
      { path: 'ranking', component: RankingComponent }
    ]
  },
  // Redirection par défaut si aucune route ne matche
  { path: '**', redirectTo: 'auth/login' }
];


