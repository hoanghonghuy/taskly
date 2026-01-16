import { createApiClient } from './apiClient';
import type { Task, CreateTaskRequest, UpdateTaskRequest } from '../types';

const apiClient = createApiClient<Task>('/api/tasks');

export const taskApi = {
  getAll: () => apiClient.getAllPaged(),
  getById: (id: string) => apiClient.getById(id),
  create: (data: CreateTaskRequest) => apiClient.create(data),
  update: (id: string, data: UpdateTaskRequest) => apiClient.update(id, data),
  delete: (id: string) => apiClient.delete(id),
};