// Auth types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

// User type
export interface User {
  id: string;
  username: string;
  email: string;
  createdAt: string;
}

// Task types
export type Priority = "LOW" | "MEDIUM" | "HIGH";
export type RecurrenceFrequency =
  | "NONE"
  | "DAILY"
  | "WEEKLY"
  | "MONTHLY"
  | "YEARLY";

export interface CreateTaskRequest {
  title: string;
  description?: string;
  dueDate?: string;
  priority: Priority;
  projectId?: string;
  tagIds?: number[];
  recurrenceFrequency?: RecurrenceFrequency;
  reminderTime?: string;
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  dueDate?: string;
  priority?: Priority;
  completed?: boolean;
  projectId?: string;
  tagIds?: number[];
  recurrenceFrequency?: RecurrenceFrequency;
  reminderTime?: string;
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  dueDate?: string;
  priority: Priority;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
  project?: Project;
  tags?: Tag[];
  recurrenceFrequency: RecurrenceFrequency;
  lastRecurrenceDate?: string;
  reminderTime?: string;
}

// Project types
export interface CreateProjectRequest {
  name: string;
  description?: string;
  color?: string;
}

export interface UpdateProjectRequest {
  name?: string;
  description?: string;
  color?: string;
}

export interface Project {
  id: string;
  name: string;
  description?: string;
  color?: string;
  createdAt: string;
  updatedAt: string;
}

// Tag types
export interface CreateTagRequest {
  name: string;
  color?: string;
}

export interface Tag {
  id: string;
  name: string;
  color?: string;
  createdAt: string;
}

// Error type
export interface ErrorResponse {
  message: string;
  status: number;
}
