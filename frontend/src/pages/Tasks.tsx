import { useState, useMemo } from "react";
import { useCRUD } from "../hooks/useCRUD";
import { taskApi } from "../lib/taskApi";
import { projectApi } from "../lib/projectApi";
import { tagApi } from "../lib/tagApi";
import type {
  Task,
  CreateTaskRequest,
  Priority,
  Project,
  Tag,
  RecurrenceFrequency,
} from "../types";
import {
  Plus,
  Check,
  Edit2,
  Trash2,
  Calendar,
  AlertCircle,
  Folder,
  Hash,
  Filter,
  X,
  Bell,
} from "lucide-react";
import Modal from "../components/Modal";
import ConfirmDialog from "../components/ConfirmDialog";

type FilterStatus = "ALL" | "ACTIVE" | "COMPLETED";
type FilterPriority = "ALL" | "LOW" | "MEDIUM" | "HIGH";
type FilterDueDate = "ALL" | "OVERDUE" | "TODAY" | "UPCOMING";

interface TasksProps {
  onTaskChange?: () => void;
}

export default function Tasks({ onTaskChange }: TasksProps) {
  const {
    items: tasks,
    loading,
    createItem,
    updateItem,
    deleteItem,
    refresh: refreshTasks,
  } = useCRUD<Task>(taskApi, true);
  const { items: projects } = useCRUD<Project>(projectApi);
  const { items: tags } = useCRUD<Tag>(tagApi);

  const [showModal, setShowModal] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [deletingTaskId, setDeletingTaskId] = useState<string | null>(null);

  // Filter states
  const [filterProject, setFilterProject] = useState<string>("");
  const [filterTag, setFilterTag] = useState<string>("");
  const [filterStatus, setFilterStatus] = useState<FilterStatus>("ALL");
  const [filterPriority, setFilterPriority] = useState<FilterPriority>("ALL");
  const [filterDueDate, setFilterDueDate] = useState<FilterDueDate>("ALL");

  const [formData, setFormData] = useState<CreateTaskRequest>({
    title: "",
    description: "",
    dueDate: "",
    priority: "MEDIUM",
    recurrenceFrequency: "NONE",
    projectId: "",
    tagIds: [],
    reminderTime: undefined,
  });

  // Filter tasks
  const filteredTasks = useMemo(() => {
    return tasks.filter((task: Task) => {
      // Filter by project
      if (filterProject && task.project?.id !== filterProject) return false;

      // Filter by tag
      if (filterTag && !task.tags?.some((t: Tag) => t.id === filterTag))
        return false;

      // Filter by status
      if (filterStatus === "ACTIVE" && task.completed) return false;
      if (filterStatus === "COMPLETED" && !task.completed) return false;

      // Filter by priority
      if (filterPriority !== "ALL" && task.priority !== filterPriority)
        return false;

      // Filter by due date
      if (filterDueDate !== "ALL") {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const taskDate = task.dueDate ? new Date(task.dueDate) : null;

        if (filterDueDate === "OVERDUE") {
          if (!taskDate || task.completed) return false;
          if (taskDate >= today) return false;
        } else if (filterDueDate === "TODAY") {
          if (!taskDate) return false;
          const taskDateStart = new Date(taskDate);
          taskDateStart.setHours(0, 0, 0, 0);
          const taskDateEnd = new Date(taskDate);
          taskDateEnd.setHours(23, 59, 59, 999);
          if (taskDateStart > today || taskDateEnd < today) return false;
        } else if (filterDueDate === "UPCOMING") {
          if (!taskDate) return false;
          const taskDateStart = new Date(taskDate);
          taskDateStart.setHours(0, 0, 0, 0);
          const tomorrow = new Date(today);
          tomorrow.setDate(tomorrow.getDate() + 1);
          if (taskDateStart < tomorrow) return false;
        }
      }

      return true;
    });
  }, [
    tasks,
    filterProject,
    filterTag,
    filterStatus,
    filterPriority,
    filterDueDate,
  ]);

  const hasActiveFilters =
    filterProject ||
    filterTag ||
    filterStatus !== "ALL" ||
    filterPriority !== "ALL" ||
    filterDueDate !== "ALL";

  const clearFilters = () => {
    setFilterProject("");
    setFilterTag("");
    setFilterStatus("ALL");
    setFilterPriority("ALL");
    setFilterDueDate("ALL");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      if (editingTask) {
        await updateItem(editingTask.id, formData);
      } else {
        await createItem(formData);
      }
      await refreshTasks();
      onTaskChange?.();

      setShowModal(false);
      setEditingTask(null);
      setFormData({
        title: "",
        description: "",
        dueDate: "",
        priority: "MEDIUM",
        recurrenceFrequency: "NONE",
        projectId: "",
        tagIds: [],
        reminderTime: undefined,
      });
    } catch (error) {
      console.error("Failed to save task:", error);
    }
  };

  const handleEdit = (task: Task) => {
    setEditingTask(task);
    setFormData({
      title: task.title,
      description: task.description || "",
      dueDate: task.dueDate || "",
      priority: task.priority,
      recurrenceFrequency: task.recurrenceFrequency,
      projectId: task.project?.id || "",
      tagIds: task.tags?.map((t) => parseInt(t.id)) || [],
      reminderTime: task.reminderTime || undefined,
    });
    setShowModal(true);
  };

  const handleDeleteClick = (id: string) => {
    setDeletingTaskId(id);
    setShowConfirm(true);
  };

  const handleDeleteConfirm = async () => {
    if (deletingTaskId) {
      await deleteItem(deletingTaskId);
      await refreshTasks();
      onTaskChange?.();
      setDeletingTaskId(null);
    }
  };

  const handleToggleComplete = async (task: Task) => {
    await updateItem(task.id, { completed: !task.completed });
    await refreshTasks();
    onTaskChange?.();
  };

  const toggleTag = (tagId: string) => {
    const tagIdNum = parseInt(tagId);
    const currentTagIds = formData.tagIds || [];
    if (currentTagIds.includes(tagIdNum)) {
      setFormData({
        ...formData,
        tagIds: currentTagIds.filter((id) => id !== tagIdNum),
      });
    } else {
      setFormData({
        ...formData,
        tagIds: [...currentTagIds, tagIdNum],
      });
    }
  };

  const getPriorityColor = (priority: Priority) => {
    const colors = {
      LOW: "bg-green-100 text-green-700 border-green-200",
      MEDIUM: "bg-yellow-100 text-yellow-700 border-yellow-200",
      HIGH: "bg-red-100 text-red-700 border-red-200",
    };
    return colors[priority];
  };

  const isOverdue = (dueDate: string | undefined) => {
    return dueDate && new Date(dueDate) < new Date();
  };

  return (
    <>
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-2">
            <Check className="w-6 h-6 text-blue-600" />
            <h2 className="text-lg font-semibold text-gray-900">Tasks</h2>
            {filteredTasks.length !== tasks.length && (
              <span className="text-sm text-gray-500">
                ({filteredTasks.length} of {tasks.length})
              </span>
            )}
          </div>
          <button
            onClick={() => {
              setEditingTask(null);
              setFormData({
                title: "",
                description: "",
                dueDate: "",
                priority: "MEDIUM",
                recurrenceFrequency: "NONE",
                projectId: "",
                tagIds: [],
                reminderTime: undefined,
              });
              setShowModal(true);
            }}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition"
          >
            <Plus className="w-4 h-4" />
            Add Task
          </button>
        </div>

        {/* Filters */}
        <div className="mb-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-gray-600" />
              <span className="text-sm font-medium text-gray-700">Filters</span>
            </div>
            {hasActiveFilters && (
              <button
                onClick={clearFilters}
                className="flex items-center gap-1 text-xs text-gray-600 hover:text-gray-900 transition"
              >
                <X className="w-3 h-3" />
                Clear all
              </button>
            )}
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
            {/* Project filter */}
            <select
              value={filterProject}
              onChange={(e) => setFilterProject(e.target.value)}
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">All Projects</option>
              {projects.map((project: Project) => (
                <option key={project.id} value={project.id}>
                  {project.name}
                </option>
              ))}
            </select>

            {/* Tag filter */}
            <select
              value={filterTag}
              onChange={(e) => setFilterTag(e.target.value)}
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">All Tags</option>
              {tags.map((tag: Tag) => (
                <option key={tag.id} value={tag.id}>
                  {tag.name}
                </option>
              ))}
            </select>

            {/* Status filter */}
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value as FilterStatus)}
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Status</option>
              <option value="ACTIVE">Active</option>
              <option value="COMPLETED">Completed</option>
            </select>

            {/* Priority filter */}
            <select
              value={filterPriority}
              onChange={(e) =>
                setFilterPriority(e.target.value as FilterPriority)
              }
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Priorities</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>

            {/* Due date filter */}
            <select
              value={filterDueDate}
              onChange={(e) =>
                setFilterDueDate(e.target.value as FilterDueDate)
              }
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Dates</option>
              <option value="OVERDUE">Overdue</option>
              <option value="TODAY">Today</option>
              <option value="UPCOMING">Upcoming</option>
            </select>
          </div>
        </div>

        {loading ? (
          <div className="text-center py-8">
            <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
            <p className="mt-2 text-sm text-gray-600">Loading tasks...</p>
          </div>
        ) : filteredTasks.length === 0 ? (
          <div className="text-center py-8 bg-gray-50 rounded-lg border-2 border-dashed border-gray-200">
            <Check className="w-10 h-10 mx-auto text-gray-400 mb-3" />
            <h3 className="text-base font-medium text-gray-900 mb-1">
              {hasActiveFilters
                ? "No tasks match your filters"
                : "No tasks yet"}
            </h3>
            <p className="text-sm text-gray-600">
              {hasActiveFilters
                ? "Try adjusting your filters"
                : "Create your first task to get started"}
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {filteredTasks.map((task: Task) => (
              <div
                key={task.id}
                className={`group p-4 border rounded-lg hover:shadow-md transition ${
                  task.completed
                    ? "bg-gray-50 border-gray-200"
                    : "bg-white border-gray-200"
                }`}
              >
                <div className="flex items-start gap-3">
                  <button
                    onClick={() => handleToggleComplete(task)}
                    className={`mt-0.5 shrink-0 w-5 h-5 rounded-full border-2 flex items-center justify-center transition ${
                      task.completed
                        ? "bg-green-500 border-green-500 text-white"
                        : "border-gray-300 hover:border-green-500"
                    }`}
                  >
                    {task.completed && <Check className="w-3.5 h-3.5" />}
                  </button>

                  <div className="flex-1 min-w-0">
                    <h3
                      className={`font-medium ${
                        task.completed
                          ? "text-gray-500 line-through"
                          : "text-gray-900"
                      }`}
                    >
                      {task.title}
                    </h3>
                    {task.description && (
                      <p className="text-sm text-gray-600 mt-1">
                        {task.description}
                      </p>
                    )}
                    <div className="flex flex-wrap items-center gap-2 mt-2">
                      <span
                        className={`px-2 py-1 text-xs font-medium rounded-full border ${getPriorityColor(
                          task.priority,
                        )}`}
                      >
                        {task.priority}
                      </span>
                      {task.project && (
                        <span
                          className="flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full border"
                          style={{
                            backgroundColor: `${task.project.color}15`,
                            borderColor: task.project.color,
                            color: task.project.color,
                          }}
                        >
                          <Folder className="w-3 h-3" />
                          {task.project.name}
                        </span>
                      )}
                      {task.tags && task.tags.length > 0 && (
                        <div className="flex gap-1 flex-wrap">
                          {task.tags.map((tag) => (
                            <span
                              key={tag.id}
                              className="flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full border"
                              style={{
                                backgroundColor: `${tag.color}15`,
                                borderColor: tag.color || "#8B5CF6",
                                color: tag.color || "#8B5CF6",
                              }}
                            >
                              <Hash className="w-3 h-3" />
                              {tag.name}
                            </span>
                          ))}
                        </div>
                      )}
                      {task.dueDate && (
                        <span
                          className={`flex items-center gap-1 text-xs ${
                            isOverdue(task.dueDate) && !task.completed
                              ? "text-red-600"
                              : "text-gray-600"
                          }`}
                        >
                          <Calendar className="w-3.5 h-3.5" />
                          {new Date(task.dueDate).toLocaleDateString()}
                        </span>
                      )}
                      {task.reminderTime && (
                        <span
                          className="flex items-center gap-1 text-xs text-amber-600"
                          title={`Reminder: ${new Date(task.reminderTime).toLocaleString()}`}
                        >
                          <Bell className="w-3.5 h-3.5" />
                        </span>
                      )}
                      {isOverdue(task.dueDate) && !task.completed && (
                        <span className="flex items-center gap-1 text-xs text-red-600">
                          <AlertCircle className="w-3.5 h-3.5" />
                          Overdue
                        </span>
                      )}
                    </div>
                  </div>

                  <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition">
                    <button
                      onClick={() => handleEdit(task)}
                      className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition"
                    >
                      <Edit2 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDeleteClick(task.id)}
                      className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal */}
      <Modal
        isOpen={showModal}
        onClose={() => {
          setShowModal(false);
          setEditingTask(null);
        }}
        title={editingTask ? "Edit Task" : "Create Task"}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="title"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Title *
            </label>
            <input
              id="title"
              type="text"
              required
              value={formData.title}
              onChange={(e) =>
                setFormData({ ...formData, title: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter task title"
            />
          </div>

          <div>
            <label
              htmlFor="description"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Description
            </label>
            <textarea
              id="description"
              value={formData.description}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              rows={3}
              placeholder="Enter task description"
            />
          </div>

          <div>
            <label
              htmlFor="project"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Project
            </label>
            <select
              id="project"
              value={formData.projectId}
              onChange={(e) =>
                setFormData({ ...formData, projectId: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">No Project (Inbox)</option>
              {projects.map((project) => (
                <option key={project.id} value={project.id}>
                  {project.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Tags
            </label>
            <div className="flex flex-wrap gap-2">
              {tags.map((tag: Tag) => {
                const tagIdNum = parseInt(tag.id);
                return (
                  <label
                    key={tag.id}
                    className={`flex items-center gap-2 px-3 py-2 rounded-full border cursor-pointer transition ${
                      formData.tagIds?.includes(tagIdNum)
                        ? "border-purple-500 bg-purple-50"
                        : "border-gray-300 hover:border-purple-400"
                    }`}
                  >
                    <input
                      type="checkbox"
                      checked={formData.tagIds?.includes(tagIdNum)}
                      onChange={() => toggleTag(tag.id)}
                      className="hidden"
                    />
                    <div
                      className="w-2 h-2 rounded-full"
                      style={{ backgroundColor: tag.color || "#8B5CF6" }}
                    />
                    <span className="text-xs font-medium">{tag.name}</span>
                  </label>
                );
              })}
              {tags.length === 0 && (
                <p className="text-xs text-gray-500">No tags available</p>
              )}
            </div>
          </div>

          <div>
            <label
              htmlFor="dueDate"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Due Date
            </label>
            <input
              id="dueDate"
              type="date"
              value={formData.dueDate}
              onChange={(e) =>
                setFormData({ ...formData, dueDate: e.target.value })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div>
            <label
              htmlFor="priority"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Priority
            </label>
            <select
              id="priority"
              value={formData.priority}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  priority: e.target.value as Priority,
                })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          <div>
            <label
              htmlFor="recurrence"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Recurrence
            </label>
            <select
              id="recurrence"
              value={formData.recurrenceFrequency}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  recurrenceFrequency: e.target.value as RecurrenceFrequency,
                })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="NONE">None</option>
              <option value="DAILY">Daily</option>
              <option value="WEEKLY">Weekly</option>
              <option value="MONTHLY">Monthly</option>
              <option value="YEARLY">Yearly</option>
            </select>
          </div>

          <div>
            <label
              htmlFor="reminder"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Reminder (Optional)
            </label>
            <input
              id="reminder"
              type="datetime-local"
              value={
                formData.reminderTime
                  ? new Date(formData.reminderTime).toISOString().slice(0, 16)
                  : ""
              }
              onChange={(e) =>
                setFormData({
                  ...formData,
                  reminderTime: e.target.value
                    ? e.target.value + ":00"
                    : undefined,
                })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
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
              className="flex-1 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition"
            >
              {editingTask ? "Update" : "Create"}
            </button>
          </div>
        </form>
      </Modal>

      {/* Confirm Dialog */}
      <ConfirmDialog
        isOpen={showConfirm}
        onClose={() => {
          setShowConfirm(false);
          setDeletingTaskId(null);
        }}
        onConfirm={handleDeleteConfirm}
        title="Delete Task"
        message="Are you sure you want to delete this task? This action cannot be undone."
      />
    </>
  );
}
