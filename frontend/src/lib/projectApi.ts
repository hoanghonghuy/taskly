import { createApiClient } from './apiClient';
import type { Project, CreateProjectRequest, UpdateProjectRequest } from '../types';

const apiClient = createApiClient<Project>('/api/projects');

export const projectApi = {
  getAll: () => apiClient.getAll(),
  getById: (id: string) => apiClient.getById(id),
  create: (data: CreateProjectRequest) => apiClient.create(data),
  update: (id: string, data: UpdateProjectRequest) => apiClient.update(id, data),
  delete: (id: string) => apiClient.delete(id),
};