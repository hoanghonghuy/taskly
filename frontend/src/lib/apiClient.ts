import api from './api';

interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export const createApiClient = <T>(basePath: string) => ({
  getAll: async () => {
    const response = await api.get<T[]>(`${basePath}`);
    return response.data;
  },

  getAllPaged: async () => {
    const response = await api.get<Page<T>>(`${basePath}`);
    return response.data;
  },

  getById: async (id: string) => {
    const response = await api.get<T>(`${basePath}/${id}`);
    return response.data;
  },

  create: async (data: Partial<T>) => {
    const response = await api.post<T>(basePath, data);
    return response.data;
  },

  update: async (id: string, data: Partial<T>) => {
    const response = await api.patch<T>(`${basePath}/${id}`, data);
    return response.data;
  },

  delete: async (id: string) => {
    await api.delete(`${basePath}/${id}`);
  },
});