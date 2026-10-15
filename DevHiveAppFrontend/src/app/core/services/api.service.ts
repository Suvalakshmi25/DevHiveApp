import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import {
  CodeSnippet, CreateTaskRequest, Member, MemberRole, Notification, Organization, Project,
  SearchResult, SnippetComment, Task, TaskComment, TaskStatus
} from '../../shared/models/workspace.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  private readonly apiUrl = 'http://localhost:8080/api';

  getOrganizations(): Observable<Organization[]> {
    return this.request('GET', '/orgs');
  }

  createOrganization(name: string): Observable<Organization> {
    return this.request('POST', '/orgs', { name });
  }

  getProjects(organizationId: number): Observable<Project[]> {
    return this.request('GET', `/projects?orgId=${organizationId}`);
  }

  createProject(name: string, organizationId: number): Observable<Project> {
    return this.request('POST', '/projects', { name, organizationId });
  }

  getProject(projectId: number): Observable<Project> {
    return this.request('GET', `/projects/${projectId}`);
  }

  updateProject(projectId: number, name: string): Observable<Project> {
    return this.request('PUT', `/projects/${projectId}`, { name });
  }

  deleteProject(projectId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/projects/${projectId}`, {
      headers: this.authHeaders(),
      responseType: 'text'
    });
  }

  getTasks(projectId: number): Observable<Task[]> {
    return this.request('GET', `/tasks?projectId=${projectId}`);
  }

  createTask(task: CreateTaskRequest): Observable<Task> {
    return this.request('POST', '/tasks', task);
  }

  updateTaskStatus(taskId: number, status: TaskStatus): Observable<Task> {
    return this.request('PATCH', `/tasks/${taskId}/status`, { status });
  }

  updateTask(taskId: number, task: { title: string; description: string; priority: string }): Observable<Task> {
    return this.request('PUT', `/tasks/${taskId}`, task);
  }

  assignTask(taskId: number, userId: number | null): Observable<Task> {
    return this.request('PATCH', `/tasks/${taskId}/assign`, { userId });
  }

  getTaskComments(taskId: number): Observable<TaskComment[]> {
    return this.request('GET', `/comments/${taskId}`);
  }

  addTaskComment(taskId: number, content: string): Observable<TaskComment> {
    return this.request('POST', `/comments/${taskId}`, { content });
  }

  updateTaskComment(commentId: number, content: string): Observable<TaskComment> {
    return this.request('PUT', `/comments/${commentId}`, { content });
  }

  deleteTaskComment(commentId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/comments/${commentId}`, {
      headers: this.authHeaders(),
      responseType: 'text'
    });
  }

  deleteTask(taskId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/tasks/${taskId}`, {
      headers: this.authHeaders(),
      responseType: 'text'
    });
  }

  getMembers(organizationId: number): Observable<Member[]> {
    return this.request('GET', `/orgs/${organizationId}/members`);
  }

  inviteMember(organizationId: number, email: string, role: MemberRole): Observable<string> {
    return this.requestText('POST', `/orgs/${organizationId}/invite`, { email, role });
  }

  updateMemberRole(organizationId: number, userId: number, role: MemberRole): Observable<string> {
    return this.requestText('PUT', `/members/${organizationId}/${userId}`, { role });
  }

  removeMember(organizationId: number, userId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/members/${organizationId}/${userId}`, {
      headers: this.authHeaders(),
      responseType: 'text'
    });
  }

  getSnippets(projectId: number): Observable<CodeSnippet[]> {
    return this.request('GET', `/snippets?projectId=${projectId}`);
  }

  createSnippet(snippet: { title: string; code: string; language: string; projectId: number }): Observable<CodeSnippet> {
    return this.request('POST', '/snippets', snippet);
  }

  getSnippetComments(snippetId: number): Observable<SnippetComment[]> {
    return this.request('GET', `/snippets/${snippetId}/comments`);
  }

  addSnippetComment(snippetId: number, comment: string, lineNumber: number | null): Observable<SnippetComment> {
    return this.request('POST', `/snippets/${snippetId}/comments`, { comment, lineNumber });
  }

  getNotifications(): Observable<Notification[]> {
    return this.request('GET', '/notifications');
  }

  markNotificationRead(notificationId: number): Observable<string> {
    return this.requestText('PATCH', `/notifications/${notificationId}`);
  }

  search(query: string): Observable<SearchResult[]> {
    return this.request('GET', `/search?q=${encodeURIComponent(query)}`);
  }

  private request<T>(method: string, path: string, body?: unknown): Observable<T> {
    return this.http.request<T>(method, `${this.apiUrl}${path}`, {
      body,
      headers: this.authHeaders()
    });
  }

  private requestText(method: string, path: string, body?: unknown): Observable<string> {
    return this.http.request(method, `${this.apiUrl}${path}`, {
      body,
      headers: this.authHeaders(),
      responseType: 'text'
    });
  }

  private authHeaders(): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${this.auth.token()}` });
  }
}
