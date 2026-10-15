import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-alert',
  template: `
    <div
      class="alert"
      [class.alert-error]="type() === 'error'"
      [class.alert-success]="type() === 'success'"
      [attr.role]="type() === 'error' ? 'alert' : 'status'">
      {{ message() }}
      @if (dismissible()) {
        <button type="button" aria-label="Dismiss" (click)="dismissed.emit()">×</button>
      }
    </div>
  `
})
export class AlertComponent {
  readonly type = input<'error' | 'success'>('error');
  readonly message = input.required<string>();
  readonly dismissible = input(false);
  readonly dismissed = output<void>();
}
