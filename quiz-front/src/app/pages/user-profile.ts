import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileService, UserQuizScore } from '../services/user-profile.service';
import {AuthService, UserInfo} from '../services/auth.service';
import {Observable, take, map} from 'rxjs';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './user-profile.html',
  styleUrls: ['./user-profile.scss']
})
export class UserProfileComponent {
  private auth = inject(AuthService);
  private profile = inject(UserProfileService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  userId: number = this.auth.getUserId()!;
  user$: Observable<UserInfo> = this.auth.getUser();

  scores$: Observable<UserQuizScore[]> = this.profile.getBestUserScores(this.userId).pipe(
    map(scores => {
      // Tri des scores du plus grand au plus petit (descendant)
      return scores.sort((a, b) => b.score - a.score);
    })
  );

  // Gestion des notifications
  successMessage: string | null = null;
  errorMessage: string | null = null;
  private notificationTimeout: any;

  // modal / form
  showEditModal = false;
  editForm: FormGroup;
  originalUser!: UserInfo;

  PASSWORD_REGEX = /^(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;

  constructor() {
    this.editForm = this.fb.group({
      username: ['',[Validators.minLength(3)]],
      email: ['',[Validators.email]],
      password: ['',[Validators.pattern(this.PASSWORD_REGEX)]],
      confirmPassword: ['']
    }, { validators: this.passwordsMatchValidator });
  }

  // Affiche un message de notification pendant 5 secondes
  private showNotification(type: 'success' | 'error', message: string): void {
    clearTimeout(this.notificationTimeout);

    if (type === 'success') {
      this.successMessage = message;
      this.errorMessage = null;
    } else {
      this.errorMessage = message;
      this.successMessage = null;
    }

    this.notificationTimeout = setTimeout(() => {
      this.successMessage = null;
      this.errorMessage = null;
    }, 5000);
  }

  openEditModal(): void {
    // Efface les notifications précédentes lors de l'ouverture du modal
    this.successMessage = null;
    this.errorMessage = null;
    clearTimeout(this.notificationTimeout);

    this.auth.getUser().pipe(take(1)).subscribe(u => {
      this.originalUser = u;
      this.editForm.patchValue({
        username: u.username,
        email: u.email,
        password: '',
        confirmPassword: ''
      });
      this.showEditModal = true;
    });
  }

  cancelEdit(): void {
    this.showEditModal = false;
    this.editForm.reset();
  }

  saveChanges(): void {
    if (this.editForm.invalid) return;

    const vals = this.editForm.value;
    const payload: any = {};
    if (vals.username && vals.username !== this.originalUser.username) payload.username = vals.username;
    if (vals.email && vals.email !== this.originalUser.email) payload.email = vals.email;
    if (vals.password) payload.password = vals.password;

    if (Object.keys(payload).length === 0) {
      this.showNotification('success', 'Aucune modification à enregistrer.');
      this.showEditModal = false;
      return;
    }


    this.auth.updateUser(payload).subscribe({
      next: () => {
        this.user$ = this.auth.getUser();
        this.showEditModal = false;

        if (payload.username) {
          // Remplacement de alert()
          this.showNotification('error', 'Nom d’utilisateur modifié : veuillez vous reconnecter.');
          this.auth.logout();
          this.router.navigate(['/auth/login']);
        } else {
          // Remplacement de alert()
          this.showNotification('success', 'Modifications enregistrées avec succès !');
        }
      },
      error: (err) => {
        console.error(err);
        // Remplacement de alert()
        this.showNotification('error', 'Erreur lors de la mise à jour : ' + (err?.message ?? 'Vérifiez la console.'));
      }
    });
  }

  private passwordsMatchValidator(group: FormGroup) {
    const p = group.get('password')?.value;
    const c = group.get('confirmPassword')?.value;

    if (p && c && p !== c) {
      group.get('confirmPassword')?.setErrors({ passwordsMismatch: true });
      return { passwordsMismatch: true };
    }

    return null;
  }
}
