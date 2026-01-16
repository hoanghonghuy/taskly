import api from './api';

export interface Stats {
  totalTasks: number;
  completedTasks: number;
  totalProjects: number;
  totalTags: number;
}

export const statsApi = {
  getStats: async () => {
    const response = await api.get<Stats>('/api/stats');
    return response.data;
  },
};