import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MemberRole, TaskComment } from '../../../shared/models/workspace.models';

@Component({
  selector: 'app-task-comments',
  imports: [FormsModule],
  templateUrl: './task-comments.component.html'
})
export class TaskCommentsComponent {
  readonly taskId = input.required<number>();
  readonly comments = input<TaskComment[]>();
  readonly expanded = input(false);
  readonly canContribute = input(false);
  readonly canAdminister = input(false);
  readonly currentRole = input<MemberRole | null>(null);
  readonly currentUserId = input<number | null>(null);

  readonly toggle = output<number>();
  readonly edit = output<{ comment: TaskComment; taskId: number }>();
  readonly delete = output<{ commentId: number; taskId: number }>();
  readonly add = output<{ taskId: number; content: string }>();

  draft = '';

  canEditComment(comment: TaskComment): boolean {
    return this.canAdminister() || (
      this.currentRole() === 'DEVELOPER' &&
      comment.userId !== undefined &&
      comment.userId === this.currentUserId()
    );
  }

  submit(): void {
    const content = this.draft.trim();
    if (!content) return;
    this.draft = '';
    this.add.emit({ taskId: this.taskId(), content });
  }
}
