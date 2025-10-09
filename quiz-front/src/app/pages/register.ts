import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup;
  error: string | null = null;

  PASSWORD_REGEX = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

  constructor() {
    this.registerForm = this.fb.group(
      {
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, this.passwordValidator.bind(this)]],
        confirmPassword: ['', Validators.required]
      },
      { validators: this.passwordsMatchValidator }
    );
  }

  onRegister() {
    if (this.registerForm.invalid) return;

    const { username, email, password } = this.registerForm.value;

    this.auth.register({ username, email, password }).subscribe({
      next: () => {
        alert('Compte créé avec succès ! Vous pouvez maintenant vous connecter.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error(err);
        this.error = 'Erreur lors de l’inscription : ' + (err?.message ?? 'voir console');
      }
    });
  }

  private passwordsMatchValidator(group: FormGroup) {
    const p = group.get('password')?.value;
    const c = group.get('confirmPassword')?.value;
    return p && c && p !== c ? { passwordsMismatch: true } : null;
  }

  private passwordValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';

    const errors: any = {};

    if (value.length < 8) {
      errors.minLength = 'Le mot de passe doit contenir au moins 8 caractères.';
    }
    if (!/[A-Z]/.test(value)) {
      errors.uppercase = 'Il manque au moins une majuscule.';
    }
    if (!/\d/.test(value)) {
      errors.digit = 'Il manque au moins un chiffre.';
    }
    if (!/[@$!%*?&]/.test(value)) {
      errors.special = 'Il manque au moins un caractère spécial (@ $ ! % * ? &).';
    }

    return Object.keys(errors).length ? errors : null;
  }

}
