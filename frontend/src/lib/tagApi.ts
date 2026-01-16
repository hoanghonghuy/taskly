import { createApiClient } from './apiClient';
import type { Tag, CreateTagRequest } from '../types';

const apiClient = createApiClient<Tag>('/api/tags');

export const tagApi = {
  getAll: () => apiClient.getAll(),
  getById: (id: string) => apiClient.getById(id),
  create: (data: CreateTagRequest) => apiClient.create(data),
  delete: (id: string) => apiClient.delete(id),
};