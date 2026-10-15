import { Component, input, output } from '@angular/core';
import { Member, MemberRole, Task, TaskComment, TaskStatus } from '../../../shared/models/workspace.models';
import { TaskCardComponent } from '../task-card/task-card.component';

@Component({
  selector: 'app-kanban-board',
  imports: [TaskCardComponent],
  templateUrl: './kanban-board.component.html'
})
export class KanbanBoardComponent {
  readonly tasks = input.required<Task[]>();
  readonly members = input.required<Member[]>();
  readonly statuses = input.required<TaskStatus[]>();
  readonly taskComments = input.required<Partial<Record<number, TaskComment[]>>>();
  readonly expandedComments = input.required<Record<number, boolean>>();
  readonly canContribute = input(false);
  readonly canAdminister = input(false);
  readonly currentRole = input<MemberRole | null>(null);
  readonly currentUserId = input<number | null>(null);
  readonly selectedProject = input(false);
  readonly projectsCount = input(0);
  readonly searchTerm = input('');

  readonly createTask = output<void>();
  readonly createProject = output<void>();
  readonly editTask = output<Task>();
  readonly deleteTask = output<Task>();
  readonly assignTask = output<{ task: Task; userId: number | null }>();
  readonly changeStatus = output<{ task: Task; status: TaskStatus }>();
  readonly toggleComments = output<number>();
  readonly editComment = output<{ comment: TaskComment; taskId: number }>();
  readonly deleteComment = output<{ commentId: number; taskId: number }>();
  readonly addComment = output<{ taskId: number; content: string }>();

  get visibleTasks(): Task[] {
    const query = this.searchTerm().trim().toLowerCase();
    return this.tasks().filter(task =>
      !query || task.title.toLowerCase().includes(query) ||
      (task.description ?? '').toLowerCase().includes(query)
    );
  }

  tasksIn(status: TaskStatus): Task[] {
    return this.visibleTasks.filter(task => task.status === status);
  }

  displayStatus(status: TaskStatus): string {
    return status === 'IN_PROGRESS' ? 'In progress' : status === 'TODO' ? 'To do' : 'Done';
  }

}
