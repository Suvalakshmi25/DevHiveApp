import { Component, input, output } from '@angular/core';
import { Notification } from '../../../shared/models/workspace.models';

@Component({
  selector: 'app-notification-list',
  templateUrl: './notification-list.component.html'
})
export class NotificationListComponent {
  readonly notifications = input.required<Notification[]>();
  readonly refresh = output<void>();
  readonly markRead = output<Notification>();
}
