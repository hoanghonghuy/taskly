import { useState } from 'react';
import { useCRUD } from '../hooks/useCRUD';
import { tagApi } from '../lib/tagApi';
import type { Tag, CreateTagRequest } from '../types';
import { Plus, Hash, XCircle } from 'lucide-react';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

interface TagsProps {
  onTagChange?: () => void;
}

export default function Tags({ onTagChange }: TagsProps) {
  const { items: tags, loading, createItem, deleteItem, refresh: refreshTags } = useCRUD<Tag>(tagApi);
  const [showModal, setShowModal] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [deletingTagId, setDeletingTagId] = useState<string | null>(null);

  const [formData, setFormData] = useState<CreateTagRequest>({
    name: '',
    color: '#8B5CF6',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await createItem(formData);
      await refreshTags();
      onTagChange?.();

      setShowModal(false);
      setFormData({
        name: '',
        color: '#8B5CF6',
      });
    } catch (error) {
      console.error('Failed to save tag:', error);
    }
  };

  const handleDeleteClick = (id: string) => {
    setDeletingTagId(id);
    setShowConfirm(true);
  };

  const handleDeleteConfirm = async () => {
    if (deletingTagId) {
      await deleteItem(deletingTagId);
      await refreshTags();
      onTagChange?.();
      setDeletingTagId(null);
    }
  };

  return (
    <>
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-2">
            <Hash className="w-6 h-6 text-purple-600" />
            <h2 className="text-lg font-semibold text-gray-900">Tags</h2>
          </div>
          <button
            onClick={() => {
              setFormData({
                name: '',
                color: '#8B5CF6',
              });
              setShowModal(true);
            }}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-purple-600 rounded-lg hover:bg-purple-700 transition"
          >
            <Plus className="w-4 h-4" />
            Add Tag
          </button>
        </div>

        {loading ? (
          <div className="text-center py-8">
            <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-purple-600"></div>
            <p className="mt-2 text-sm text-gray-600">Loading tags...</p>
          </div>
        ) : tags.length === 0 ? (
          <div className="text-center py-8 bg-gray-50 rounded-lg border-2 border-dashed border-gray-200">
            <Hash className="w-10 h-10 mx-auto text-gray-400 mb-3" />
            <h3 className="text-base font-medium text-gray-900 mb-1">No tags yet</h3>
            <p className="text-sm text-gray-600">Create tags to categorize your tasks</p>
          </div>
        ) : (
          <div className="flex flex-wrap gap-2">
            {tags.map((tag: Tag) => (
              <div
                key={tag.id}
                className="inline-flex items-center gap-2 px-3 py-2 rounded-full border hover:shadow-md transition group"
                style={{
                  backgroundColor: `${tag.color}15`,
                  borderColor: tag.color || '#8B5CF6',
                }}
              >
                <div
                  className="w-2 h-2 rounded-full shrink-0"
                  style={{ backgroundColor: tag.color || '#8B5CF6' }}
                />
                <span className="text-sm font-medium text-gray-800">{tag.name}</span>
                <button
                  onClick={() => handleDeleteClick(tag.id)}
                  className="ml-1 p-0.5 text-gray-400 hover:text-red-600 transition opacity-0 group-hover:opacity-100"
                >
                  <XCircle className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal */}
      <Modal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        title="Create Tag"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
              Name *
            </label>
            <input
              id="name"
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Enter tag name"
            />
          </div>

          <div>
            <label htmlFor="color" className="block text-sm font-medium text-gray-700 mb-2">
              Color
            </label>
            <div className="flex gap-3 items-center">
              <input
                id="color"
                type="color"
                value={formData.color}
                onChange={(e) => setFormData({ ...formData, color: e.target.value })}
                className="h-10 w-10 rounded cursor-pointer border-0"
              />
              <input
                type="text"
                value={formData.color}
                onChange={(e) => setFormData({ ...formData, color: e.target.value })}
                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="#8B5CF6"
              />
              <div
                className="w-10 h-10 rounded-lg border-2 border-gray-200"
                style={{ backgroundColor: formData.color }}
              />
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={() => setShowModal(false)}
              className="flex-1 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex-1 px-4 py-2 text-sm font-medium text-white bg-purple-600 rounded-lg hover:bg-purple-700 transition"
            >
              Create
            </button>
          </div>
        </form>
      </Modal>

      {/* Confirm Dialog */}
      <ConfirmDialog
        isOpen={showConfirm}
        onClose={() => {
          setShowConfirm(false);
          setDeletingTagId(null);
        }}
        onConfirm={handleDeleteConfirm}
        title="Delete Tag"
        message="Are you sure you want to delete this tag? This action cannot be undone."
      />
    </>
  );
}