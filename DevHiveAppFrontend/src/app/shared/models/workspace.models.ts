export interface AuthResponse {
  token: string;
}

export interface Organization {
  id: number;
  name: string;
}

export interface Project {
  id: number;
  name: string;
}

export interface Member {
  userId: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'DEVELOPER' | 'VIEWER';
}

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
export type MemberRole = Member['role'];

export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  priority: Priority;
  assignedTo: number | null;
}

export interface TaskComment {
  id: number;
  content: string;
  userId?: number;
  userName: string;
  createdAt: string;
}

export interface CodeSnippet {
  id: number;
  title: string;
  code: string;
  language: string;
}

export interface SnippetComment {
  id: number;
  comment: string;
  lineNumber: number | null;
  userName: string;
}

export interface Notification {
  id: number;
  message: string;
  read: boolean;
  createdAt: string;
}

export interface SearchResult {
  type: 'TASK' | 'SNIPPET';
  id: number;
  title: string;
}

export interface TaskDraft {
  title: string;
  description: string;
  priority: Priority;
}

export interface CreateTaskRequest extends TaskDraft {
  projectId: number;
}
