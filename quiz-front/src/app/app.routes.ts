import {Routes} from '@angular/router';
import {QuizComponent} from './quiz/quiz';
import {HomeComponent} from './home/home';
import { QuizListComponent } from './pages/quiz-list';
import QuizSelectionComponent from './pages/type-selection';
import {LayoutComponent} from './layout/layout';

export const routes: Routes = [
  { path: '',
    component: LayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'quiz/:quizId', component: QuizComponent },
      { path: 'quizzes/type/:type', component: QuizListComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];

