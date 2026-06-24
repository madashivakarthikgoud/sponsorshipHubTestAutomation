import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {
  changePasswordForm: FormGroup;
  isLoading = false;
  hideOldPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;
  passwordMismatch = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ChangePasswordComponent>
  ) {
    this.changePasswordForm = this.formBuilder.group({
      oldPassword: ['', [Validators.required]],
      newPassword: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d)(?=.*@).{6,}$/)
        ]
      ],
      confirmPassword: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    // Subscribe to form changes to check password match in real-time
    this.changePasswordForm.get('newPassword')?.valueChanges.subscribe(() => {
      this.checkPasswordMatch();
    });
    this.changePasswordForm.get('confirmPassword')?.valueChanges.subscribe(() => {
      this.checkPasswordMatch();
    });
  }

  checkPasswordMatch(): void {
    const newPassword = this.changePasswordForm.get('newPassword')?.value;
    const confirmPassword = this.changePasswordForm.get('confirmPassword')?.value;
    
    // Only check if both fields have values
    if (newPassword && confirmPassword) {
      this.passwordMismatch = newPassword !== confirmPassword;
    } else {
      this.passwordMismatch = false;
    }
  }

  isSubmitDisabled(): boolean {
    const oldPassword = this.changePasswordForm.get('oldPassword');
    const newPassword = this.changePasswordForm.get('newPassword');
    const confirmPassword = this.changePasswordForm.get('confirmPassword');
    
    // Check if all required fields are filled and valid
    const allFieldsFilled = oldPassword?.valid && newPassword?.valid && confirmPassword?.valid;
    
    // Check if passwords match
    const passwordsMatch = !this.passwordMismatch;
    
    return !(allFieldsFilled && passwordsMatch) || this.isLoading;
  }

  onSubmit(): void {
    if (this.isSubmitDisabled()) {
      if (this.passwordMismatch) {
        this.snackBar.open('Passwords did not match', 'Close', { duration: 3000 });
      } else {
        this.snackBar.open('Please fill all fields correctly', 'Close', { duration: 3000 });
      }
      return;
    }

    this.isLoading = true;
    const { oldPassword, newPassword, confirmPassword } = this.changePasswordForm.value;

    this.authService.changePassword(oldPassword, newPassword, confirmPassword).subscribe({
      next: () => {
        this.snackBar.open('Password changed successfully!', 'Close', { duration: 3000 });
        this.changePasswordForm.reset();
        this.passwordMismatch = false;
        this.isLoading = false;
        // Close the dialog after a short delay to allow success message to display
        setTimeout(() => {
          this.dialogRef.close(true);
        }, 1000);
      },
      error: (error) => {
        this.snackBar.open(
          error.error?.message || 'Failed to change password',
          'Close',
          { duration: 3000 }
        );
        this.isLoading = false;
      }
    });
  }

  togglePasswordVisibility(field: 'old' | 'new' | 'confirm'): void {
    switch (field) {
      case 'old':
        this.hideOldPassword = !this.hideOldPassword;
        break;
      case 'new':
        this.hideNewPassword = !this.hideNewPassword;
        break;
      case 'confirm':
        this.hideConfirmPassword = !this.hideConfirmPassword;
        break;
    }
  }
}
