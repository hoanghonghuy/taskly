import { useState, useEffect } from 'react';

interface CRUDApi<T> {
  getAll: () => Promise<T[] | { content: T[] }>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  create: (data: any) => Promise<T>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  update?: (id: string, data: any) => Promise<T>;
  delete: (id: string) => Promise<void>;
}

interface UseCRUDReturn<T> {
  items: T[];
  loading: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  createItem: (data: any) => Promise<void>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  updateItem: (id: string, data: any) => Promise<void>;
  deleteItem: (id: string) => Promise<void>;
  refresh: () => Promise<void>;
}

export function useCRUD<T extends { id: string }>(
  api: CRUDApi<T>,
  isPaged: boolean = false
): UseCRUDReturn<T> {
  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchItems = async () => {
    try {
      setLoading(true);
      const response = await api.getAll();
      const data = isPaged && 'content' in response ? response.content : response;
      setItems(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Failed to fetch items:', error);
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const createItem = async (data: any) => {
    await api.create(data);
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const updateItem = async (id: string, data: any): Promise<void> => {
    if (api.update) {
      await api.update(id, data);
    }
  };

  const deleteItem = async (id: string): Promise<void> => {
    await api.delete(id);
  };

  useEffect(() => {
    fetchItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return {
    items,
    loading,
    createItem,
    updateItem,
    deleteItem,
    refresh: fetchItems,
  };
}