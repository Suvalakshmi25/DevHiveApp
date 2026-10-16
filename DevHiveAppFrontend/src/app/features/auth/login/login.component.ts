import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertComponent } from '../../../shared/components/alert/alert.component';
import { RegisterComponent } from '../register/register.component';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, AlertComponent, RegisterComponent],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private readonly authService = inject(AuthService);

  readonly notice = input('');
  authMode: 'login' | 'register' = 'login';
  auth = { email: '', password: '' };
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly loading = signal(false);

  setAuthMode(mode: 'login' | 'register'): void {
    this.authMode = mode;
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  registrationComplete(): void {
    this.setAuthMode('login');
    this.successMessage.set('Account created. Sign in to continue.');
  }

  submitAuth(): void {
    this.errorMessage.set('');
    this.loading.set(true);

    this.authService.login({ email: this.auth.email, password: this.auth.password }).subscribe({
      next: () => this.loading.set(false),
      error: error => this.handleError(error)
    });
  }

  private handleError(error: unknown): void {
    this.loading.set(false);
    const response = error as HttpErrorResponse;
    if (response.status === 0) {
      this.errorMessage.set('Could not reach DevHive. Check that the backend is running at localhost:8080.');
      return;
    }

    const serverMessage = typeof response.error === 'string'
      ? response.error
      : response.error?.message;
    this.errorMessage.set(serverMessage || `Something went wrong (${response.status}). Please try again.`);
  }
}
