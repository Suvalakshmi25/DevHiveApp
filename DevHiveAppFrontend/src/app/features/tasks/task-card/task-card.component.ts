import { Component, computed, input, output } from '@angular/core';
import {
  Member, MemberRole, Task, TaskComment, TaskStatus
} from '../../../shared/models/workspace.models';
import { TaskCommentsComponent } from '../../comments/task-comments/task-comments.component';
import { SelectOption, StyledSelectComponent } from '../../../shared/components/styled-select/styled-select.component';

@Component({
  selector: 'app-task-card',
  imports: [TaskCommentsComponent, StyledSelectComponent],
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
  readonly memberOptions = computed<SelectOption<number | null>[]>(() => [
    { label: 'Unassigned', value: null },
    ...this.members().map(member => ({ label: member.name, value: member.userId }))
  ]);
  readonly statusOptions = computed<SelectOption<TaskStatus>[]>(() =>
    this.statuses().map(status => ({ label: this.displayStatus(status), value: status }))
  );

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
