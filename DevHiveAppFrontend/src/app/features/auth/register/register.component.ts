import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AlertComponent } from '../../../shared/components/alert/alert.component';

@Component({
  selector: 'app-register',
  imports: [FormsModule, AlertComponent],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  private readonly authService = inject(AuthService);
  readonly registered = output<void>();
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  details = { name: '', email: '', password: '' };

  submit(): void {
    this.errorMessage.set('');
    this.loading.set(true);
    this.authService.register(this.details).subscribe({
      next: () => {
        this.loading.set(false);
        this.registered.emit();
      },
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
