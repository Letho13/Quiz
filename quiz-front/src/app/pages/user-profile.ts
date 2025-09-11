import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileService, UserQuizScore } from '../services/user-profile.service';
import { AuthService } from '../services/auth.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-profile.html',
  styleUrls: ['./user-profile.scss']
})
export class UserProfileComponent {
  private auth = inject(AuthService);
  private profile = inject(UserProfileService);

  userId: number = this.auth.getUserId()!;

  userUsername$: Observable<string> = this.auth.getUsername();
  userEmail$: Observable<string> = this.auth.getUserEmail();
  scores$: Observable<UserQuizScore[]> = this.profile.getUserScores(this.userId);

}
