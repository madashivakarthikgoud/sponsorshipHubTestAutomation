import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  private readonly passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*@).{6,}$/;
  signupForm: FormGroup;
  isLoading = false;
  hidePassword = true;
  roles = [
    { value: 'BRAND', label: 'Brand' },
    { value: 'INFLUENCER', label: 'Influencer' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.signupForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.pattern(this.passwordPattern)]],
      role: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const payload = {
      ...this.signupForm.value,
      name: this.signupForm.value.name?.trim(),
      email: this.signupForm.value.email?.trim().toLowerCase()
    };

    this.authService.register(payload).subscribe({
      next: () => {
        this.snackBar.open('Registration successful!', 'Close', { duration: 3000 });
        this.router.navigate([this.authService.getDashboardRoute()]);
      },
      error: (error) => {
        this.isLoading = false;
        const message = this.extractErrorMessage(error);
        this.snackBar.open(message, 'Close', { duration: 3500 });
      }
    });
  }

  private extractErrorMessage(error: any): string {
    if (error?.error?.name) {
      return error.error.name;
    }
    if (error?.error?.password) {
      return error.error.password;
    }
    if (error?.error?.email) {
      return error.error.email;
    }
    return error?.error?.message || 'Registration failed';
  }
}

