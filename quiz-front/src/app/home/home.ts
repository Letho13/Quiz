import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import QuizSelectionComponent from '../pages/type-selection';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, QuizSelectionComponent],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent {

}
