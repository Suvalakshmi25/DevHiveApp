import { isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { afterNextRender, Component, inject, PLATFORM_ID, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  CodeSnippet, Member, MemberRole, Notification, Organization, Priority, Project, SearchResult,
  SnippetComment, Task, TaskComment, TaskDraft, TaskStatus
} from '../../shared/models/workspace.models';
import { AuthService } from '../../core/services/auth.service';
import { ApiService } from '../../core/services/api.service';
import { WebsocketService } from '../../core/services/websocket.service';
import { OrganizationMembersComponent } from '../organization/members/organization-members.component';
import { SnippetLibraryComponent } from '../snippets/snippet-library/snippet-library.component';
import { NotificationListComponent } from '../notifications/notification-list/notification-list.component';
import { KanbanBoardComponent } from '../tasks/kanban-board/kanban-board.component';
import { ProjectListComponent } from '../projects/project-list/project-list.component';
import { ProjectDetailComponent } from '../projects/project-detail/project-detail.component';

@Component({
  selector: 'app-dashboard',
  imports: [
    FormsModule,
    KanbanBoardComponent,
    OrganizationMembersComponent,
    SnippetLibraryComponent,
    NotificationListComponent,
    ProjectListComponent,
    ProjectDetailComponent
  ],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
  private readonly auth = inject(AuthService);
  private readonly workspace = inject(ApiService);
  private readonly websocket = inject(WebsocketService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly organizations = signal<Organization[]>([]);
  readonly projects = signal<Project[]>([]);
  readonly tasks = signal<Task[]>([]);
  readonly selectedOrganizationId = signal<number | null>(null);
  readonly selectedProjectId = signal<number | null>(null);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly dialog = signal<'organization' | 'project' | 'task' | 'edit-task' | 'edit-project' | 'snippet' | null>(null);
  readonly activeSection = signal<'overview' | 'members' | 'snippets' | 'notifications'>('overview');
  readonly members = signal<Member[]>([]);
  readonly currentRole = signal<MemberRole | null>(null);
  readonly currentUserId = signal<number | null>(null);
  readonly snippets = signal<CodeSnippet[]>([]);
  readonly notifications = signal<Notification[]>([]);
  readonly searchResults = signal<SearchResult[]>([]);
  readonly taskComments = signal<Partial<Record<number, TaskComment[]>>>({});
  readonly snippetComments = signal<Partial<Record<number, SnippetComment[]>>>({});
  readonly expandedComments = signal<Record<number, boolean>>({});
  readonly roles: MemberRole[] = ['ADMIN', 'DEVELOPER', 'VIEWER'];

  searchTerm = '';
  organizationName = '';
  projectName = '';
  snippetDraft = { title: '', code: '', language: 'text' };
  snippetCommentDrafts: Record<number, { comment: string; lineNumber: number | null }> = {};
  taskEditDraft: TaskDraft = { title: '', description: '', priority: 'MEDIUM' };
  projectEditName = '';
  private editingTaskId: number | null = null;
  private searchTimer?: ReturnType<typeof setTimeout>;
  taskDraft: TaskDraft = { title: '', description: '', priority: 'MEDIUM' };
  private messageTimer?: ReturnType<typeof setTimeout>;

  readonly statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];
  readonly priorities: Priority[] = ['LOW', 'MEDIUM', 'HIGH'];

  constructor() {
    afterNextRender(() => {
      if (isPlatformBrowser(this.platformId)) {
        window.requestAnimationFrame(() => this.loadOrganizations());
      }
    });
  }

  get canAdminister(): boolean {
    return this.currentRole() === 'ADMIN';
  }

  get canContribute(): boolean {
    return this.currentRole() === 'ADMIN' || this.currentRole() === 'DEVELOPER';
  }

  get selectedOrganization(): Organization | undefined {
    return this.organizations().find(org => org.id === this.selectedOrganizationId());
  }

  get selectedProject(): Project | undefined {
    return this.projects().find(project => project.id === this.selectedProjectId());
  }

  get completedCount(): number {
    return this.tasks().filter(task => task.status === 'DONE').length;
  }

  get inProgressCount(): number {
    return this.tasks().filter(task => task.status === 'IN_PROGRESS').length;
  }

  get completionPercent(): number {
    return this.tasks().length ? Math.round(this.completedCount / this.tasks().length * 100) : 0;
  }

  get initials(): string {
    return (this.auth.userEmail()[0] ?? 'D').toUpperCase();
  }

  get userEmail(): string {
    return this.auth.userEmail() || 'My account';
  }

  loadOrganizations(): void {
    this.workspace.getOrganizations().subscribe({
      next: organizations => {
        this.organizations.set(organizations);
        if (!organizations.some(org => org.id === this.selectedOrganizationId())) {
          this.selectedOrganizationId.set(organizations[0]?.id ?? null);
        }
        this.loadProjects();
      },
      error: error => this.handleError(error)
    });
  }

  selectOrganization(value: number | null): void {
    this.selectedOrganizationId.set(value);
    this.selectedProjectId.set(null);
    this.projects.set([]);
    this.tasks.set([]);
    this.members.set([]);
    this.currentRole.set(null);
    this.currentUserId.set(null);
    this.loadProjects();
  }

  loadProjects(): void {
    const organizationId = this.selectedOrganizationId();
    if (organizationId === null) return;

    this.workspace.getProjects(organizationId).subscribe({
      next: projects => {
        this.projects.set(projects);
        if (!projects.some(project => project.id === this.selectedProjectId())) {
          this.selectedProjectId.set(projects[0]?.id ?? null);
        }
        this.loadTasks();
        this.loadMembers();
        this.loadSnippets();
      },
      error: error => this.handleError(error)
    });
  }

  selectProject(project: Project): void {
    this.selectedProjectId.set(project.id);
    this.workspace.getProject(project.id).subscribe({
      next: details => this.projects.update(items => items.map(item => item.id === details.id ? details : item)),
      error: error => this.handleError(error)
    });
    this.loadTasks();
    this.loadSnippets();
  }

  loadTasks(): void {
    const projectId = this.selectedProjectId();
    if (projectId === null) {
      this.tasks.set([]);
      return;
    }

    this.workspace.getTasks(projectId).subscribe({
      next: tasks => this.tasks.set(tasks),
      error: error => this.handleError(error)
    });
  }

  openDialog(type: 'organization' | 'project' | 'task' | 'edit-task' | 'edit-project' | 'snippet'): void {
    this.errorMessage.set('');
    this.dialog.set(type);
  }

  openEditTask(task: Task): void {
    this.editingTaskId = task.id;
    this.taskEditDraft = {
      title: task.title,
      description: task.description ?? '',
      priority: task.priority
    };
    this.openDialog('edit-task');
  }

  openEditProject(): void {
    const project = this.selectedProject;
    if (!project) return;
    this.projectEditName = project.name;
    this.openDialog('edit-project');
  }

  closeDialog(): void {
    this.dialog.set(null);
    this.errorMessage.set('');
    this.editingTaskId = null;
  }

  createOrganization(): void {
    this.workspace.createOrganization(this.organizationName.trim()).subscribe({
      next: organization => {
        this.organizations.update(organizations => [...organizations, organization]);
        this.selectedOrganizationId.set(organization.id);
        this.organizationName = '';
        this.closeDialog();
        this.loadProjects();
        this.showMessage('Your organization is ready.');
      },
      error: error => this.handleError(error)
    });
  }

  createProject(): void {
    if (!this.canAdminister) return;
    const organizationId = this.selectedOrganizationId();
    if (organizationId === null) return;

    this.workspace.createProject(this.projectName.trim(), organizationId).subscribe({
      next: project => {
        this.projects.update(projects => [...projects, project]);
        this.selectedProjectId.set(project.id);
        this.projectName = '';
        this.closeDialog();
        this.loadTasks();
        this.loadSnippets();
        this.showMessage('Project created.');
      },
      error: error => this.handleError(error)
    });
  }

  createTask(): void {
    if (!this.canContribute) return;
    const projectId = this.selectedProjectId();
    if (projectId === null) return;

    this.workspace.createTask({
      ...this.taskDraft,
      title: this.taskDraft.title.trim(),
      description: this.taskDraft.description.trim(),
      projectId
    }).subscribe({
      next: task => {
        this.tasks.update(tasks => [...tasks, task]);
        this.taskDraft = { title: '', description: '', priority: 'MEDIUM' };
        this.closeDialog();
        this.showMessage('Task added to the board.');
      },
      error: error => this.handleError(error)
    });
  }

  saveTask(): void {
    if (!this.canContribute) return;
    const taskId = this.editingTaskId;
    if (taskId === null) return;
    this.workspace.updateTask(taskId, {
      ...this.taskEditDraft,
      title: this.taskEditDraft.title.trim(),
      description: this.taskEditDraft.description.trim()
    }).subscribe({
      next: updated => {
        this.tasks.update(tasks => tasks.map(task => task.id === updated.id ? updated : task));
        this.closeDialog();
        this.showMessage('Task updated.');
      },
      error: error => this.handleError(error)
    });
  }

  saveProject(): void {
    if (!this.canAdminister) return;
    const project = this.selectedProject;
    if (!project) return;
    this.workspace.updateProject(project.id, this.projectEditName.trim()).subscribe({
      next: updated => {
        this.projects.update(projects => projects.map(item => item.id === updated.id ? updated : item));
        this.closeDialog();
        this.showMessage('Project updated.');
      },
      error: error => this.handleError(error)
    });
  }

  deleteProject(): void {
    if (!this.canAdminister) return;
    const project = this.selectedProject;
    if (!project || !isPlatformBrowser(this.platformId) || !window.confirm(`Delete project "${project.name}" and its tasks?`)) return;
    this.workspace.deleteProject(project.id).subscribe({
      next: () => {
        this.projects.update(projects => projects.filter(item => item.id !== project.id));
        this.selectedProjectId.set(null);
        this.tasks.set([]);
        this.loadProjects();
        this.showMessage('Project deleted.');
      },
      error: error => this.handleError(error)
    });
  }

  assignTask(task: Task, userId: number | null): void {
    if (!this.canContribute) return;
    this.workspace.assignTask(task.id, userId).subscribe({
      next: updated => this.tasks.update(tasks => tasks.map(item => item.id === updated.id ? updated : item)),
      error: error => this.handleError(error)
    });
  }

  toggleTaskComments(taskId: number): void {
    const expanded = !this.expandedComments()[taskId];
    this.expandedComments.update(state => ({ ...state, [taskId]: expanded }));
    if (expanded && !this.taskComments()[taskId]) this.loadTaskComments(taskId);
  }

  loadTaskComments(taskId: number): void {
    this.workspace.getTaskComments(taskId).subscribe({
      next: comments => this.taskComments.update(state => ({ ...state, [taskId]: comments })),
      error: error => this.handleError(error)
    });
  }

  addTaskComment(taskId: number, content: string): void {
    if (!this.canContribute) return;
    const trimmedContent = content.trim();
    if (!trimmedContent) return;
    this.workspace.addTaskComment(taskId, trimmedContent).subscribe({
      next: comment => {
        this.taskComments.update(state => ({ ...state, [taskId]: [...(state[taskId] ?? []), comment] }));
      },
      error: error => this.handleError(error)
    });
  }

  editTaskComment(comment: TaskComment, taskId: number): void {
    if (!this.canAdminister) return;
    const content = (window.prompt('Edit comment', comment.content) ?? '').trim();
    if (!content || content === comment.content) return;
    this.workspace.updateTaskComment(comment.id, content).subscribe({
      next: updated => this.taskComments.update(state => ({
        ...state,
        [taskId]: (state[taskId] ?? []).map(item => item.id === updated.id ? updated : item)
      })),
      error: error => this.handleError(error)
    });
  }

  deleteTaskComment(commentId: number, taskId: number): void {
    if (!this.canAdminister) return;
    if (!isPlatformBrowser(this.platformId) || !window.confirm('Delete this comment?')) return;
    this.workspace.deleteTaskComment(commentId).subscribe({
      next: () => this.taskComments.update(state => ({
        ...state,
        [taskId]: (state[taskId] ?? []).filter(comment => comment.id !== commentId)
      })),
      error: error => this.handleError(error)
    });
  }

  loadMembers(): void {
    const organizationId = this.selectedOrganizationId();
    if (organizationId === null) return;
    this.workspace.getMembers(organizationId).subscribe({
      next: members => {
        this.members.set(members);
        const currentMember = members.find(member => member.email.toLowerCase() === this.auth.userEmail().toLowerCase());
        this.currentRole.set(currentMember?.role ?? null);
        this.currentUserId.set(currentMember?.userId ?? null);
        if (currentMember) this.connectNotifications(currentMember.userId);
      },
      error: error => this.handleError(error)
    });
  }

  private connectNotifications(userId: number): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.websocket.connect(
      userId,
      () => this.loadNotifications(),
      () => this.errorMessage.set('Live notifications are unavailable. Refresh notifications to check for updates.')
    );
  }

  inviteMember(email: string, role: MemberRole): void {
    if (!this.canAdminister) return;
    const organizationId = this.selectedOrganizationId();
    if (organizationId === null) return;
    this.workspace.inviteMember(organizationId, email, role).subscribe({
      next: () => {
        this.loadMembers();
        this.showMessage('Member invited.');
      },
      error: error => this.handleError(error)
    });
  }

  changeMemberRole(member: Member, role: MemberRole): void {
    if (!this.canAdminister) return;
    const organizationId = this.selectedOrganizationId();
    if (organizationId === null || member.role === role) return;
    this.workspace.updateMemberRole(organizationId, member.userId, role).subscribe({
      next: () => {
        this.members.update(items => items.map(item => item.userId === member.userId ? { ...item, role } : item));
        if (member.userId === this.currentUserId()) this.currentRole.set(role);
      },
      error: error => this.handleError(error)
    });
  }

  removeMember(member: Member): void {
    if (!this.canAdminister) return;
    const organizationId = this.selectedOrganizationId();
    if (organizationId === null || !isPlatformBrowser(this.platformId) || !window.confirm(`Remove ${member.name} from this organization?`)) return;
    this.workspace.removeMember(organizationId, member.userId).subscribe({
      next: () => {
        this.members.update(items => items.filter(item => item.userId !== member.userId));
        if (member.userId === this.currentUserId()) {
          this.currentRole.set(null);
          this.currentUserId.set(null);
        }
      },
      error: error => this.handleError(error)
    });
  }

  loadSnippets(): void {
    const projectId = this.selectedProjectId();
    if (projectId === null) {
      this.snippets.set([]);
      return;
    }
    this.workspace.getSnippets(projectId).subscribe({
      next: snippets => this.snippets.set(snippets),
      error: error => this.handleError(error)
    });
  }

  createSnippet(): void {
    if (!this.canContribute) return;
    const projectId = this.selectedProjectId();
    if (projectId === null) return;
    this.workspace.createSnippet({ ...this.snippetDraft, projectId }).subscribe({
      next: snippet => {
        this.snippets.update(items => [...items, snippet]);
        this.snippetDraft = { title: '', code: '', language: 'text' };
        this.closeDialog();
        this.showMessage('Snippet saved.');
      },
      error: error => this.handleError(error)
    });
  }

  toggleSnippetComments(snippet: CodeSnippet): void {
    if (this.snippetComments()[snippet.id]) return;
    this.snippetCommentDrafts[snippet.id] = { comment: '', lineNumber: null };
    this.workspace.getSnippetComments(snippet.id).subscribe({
      next: comments => this.snippetComments.update(state => ({ ...state, [snippet.id]: comments })),
      error: error => this.handleError(error)
    });
  }

  addSnippetComment(snippetId: number, comment: string, lineNumber: number | null): void {
    if (!this.canContribute || !comment.trim()) return;
    this.workspace.addSnippetComment(snippetId, comment.trim(), lineNumber).subscribe({
      next: created => {
        this.snippetComments.update(state => ({ ...state, [snippetId]: [...(state[snippetId] ?? []), created] }));
        this.snippetCommentDrafts[snippetId] = { comment: '', lineNumber: null };
      },
      error: error => this.handleError(error)
    });
  }

  loadNotifications(): void {
    this.workspace.getNotifications().subscribe({
      next: notifications => this.notifications.set(notifications),
      error: error => this.handleError(error)
    });
  }

  markNotificationRead(notification: Notification): void {
    if (notification.read) return;
    this.workspace.markNotificationRead(notification.id).subscribe({
      next: () => this.notifications.update(items => items.map(item => item.id === notification.id ? { ...item, read: true } : item)),
      error: error => this.handleError(error)
    });
  }

  searchBackend(query: string): void {
    if (this.searchTimer) clearTimeout(this.searchTimer);
    if (!query.trim()) {
      this.searchResults.set([]);
      return;
    }
    this.searchTimer = setTimeout(() => this.workspace.search(query.trim()).subscribe({
      next: results => this.searchResults.set(results),
      error: error => this.handleError(error)
    }), 250);
  }

  openSection(section: 'overview' | 'members' | 'snippets' | 'notifications'): void {
    this.activeSection.set(section);
    if (section === 'members') this.loadMembers();
    if (section === 'snippets') this.loadSnippets();
    if (section === 'notifications') this.loadNotifications();
  }

  changeStatus(task: Task, status: TaskStatus): void {
    if (!this.canContribute) return;
    if (task.status === status) return;
    this.workspace.updateTaskStatus(task.id, status).subscribe({
      next: updated => this.tasks.update(tasks => tasks.map(item => item.id === task.id ? updated : item)),
      error: error => this.handleError(error)
    });
  }

  deleteTask(task: Task): void {
    if (!this.canAdminister) return;
    if (!isPlatformBrowser(this.platformId) || !window.confirm(`Delete "${task.title}"?`)) return;
    this.workspace.deleteTask(task.id).subscribe({
      next: () => {
        this.tasks.update(tasks => tasks.filter(item => item.id !== task.id));
        this.showMessage('Task deleted.');
      },
      error: error => this.handleError(error)
    });
  }

  logout(): void {
    this.websocket.disconnect();
    this.auth.logout();
    this.organizations.set([]);
    this.projects.set([]);
    this.tasks.set([]);
    this.members.set([]);
    this.currentRole.set(null);
    this.currentUserId.set(null);
    this.selectedOrganizationId.set(null);
    this.selectedProjectId.set(null);
  }

  private handleError(error: unknown): void {
    const response = error as HttpErrorResponse;
    if (response.status === 401) {
      this.logout();
      this.errorMessage.set('Your session has expired. Please sign in again.');
    } else if (response.status === 403) {
      this.errorMessage.set('Your organization role does not allow that action.');
    } else if (response.status === 0) {
      this.errorMessage.set('Could not reach DevHive. Check that the backend is running at localhost:8080.');
    } else {
      const serverMessage = typeof response.error === 'string'
        ? response.error
        : response.error?.message;
      this.errorMessage.set(serverMessage || `Something went wrong (${response.status}). Please try again.`);
    }
  }

  private showMessage(message: string): void {
    this.successMessage.set(message);
    if (this.messageTimer) clearTimeout(this.messageTimer);
    this.messageTimer = setTimeout(() => this.successMessage.set(''), 3200);
  }
}
