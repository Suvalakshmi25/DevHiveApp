import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Member, MemberRole, Task, TaskComment, TaskStatus
} from '../../../shared/models/workspace.models';
import { TaskCommentsComponent } from '../../comments/task-comments/task-comments.component';

@Component({
  selector: 'app-task-card',
  imports: [FormsModule, TaskCommentsComponent],
  templateUrl: './task-card.component.html'
})
export class TaskCardComponent {
  readonly task = input.required<Task>();
  readonly members = input<Member[]>([]);
  readonly statuses = input.required<TaskStatus[]>();
  readonly comments = input<TaskComment[]>();
  readonly commentsExpanded = input(false);
  readonly canContribute = input(false);
  readonly canAdminister = input(false);
  readonly currentRole = input<MemberRole | null>(null);
  readonly currentUserId = input<number | null>(null);

  readonly editTask = output<Task>();
  readonly deleteTask = output<Task>();
  readonly assign = output<{ task: Task; userId: number | null }>();
  readonly changeStatus = output<{ task: Task; status: TaskStatus }>();
  readonly toggleComments = output<number>();
  readonly editComment = output<{ comment: TaskComment; taskId: number }>();
  readonly deleteComment = output<{ commentId: number; taskId: number }>();
  readonly addComment = output<{ taskId: number; content: string }>();

  displayStatus(status: TaskStatus): string {
    return status === 'IN_PROGRESS' ? 'In progress' : status === 'TODO' ? 'To do' : 'Done';
  }
}
