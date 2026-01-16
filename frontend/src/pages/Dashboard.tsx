import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { LogOut, LayoutDashboard, CheckSquare2, FolderOpen, CheckCircle, Tags as TagsIcon } from 'lucide-react';
import { useStats } from '../hooks/useStats';
import Tasks from './Tasks';
import Projects from './Projects';
import Tags from './Tags';

export default function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { stats, loading: statsLoading, refresh: refreshStats } = useStats();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-blue-600 rounded-lg">
                <LayoutDashboard className="w-6 h-6 text-white" />
              </div>
              <h1 className="text-2xl font-bold text-gray-900">Taskly</h1>
            </div>
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center">
                  <span className="text-sm font-medium text-blue-600">
                    {user?.username?.charAt(0).toUpperCase() || 'U'}
                  </span>
                </div>
                <span className="text-sm font-medium text-gray-700 hidden sm:block">
                  {user?.username || 'User'}
                </span>
              </div>
              <button
                onClick={handleLogout}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition"
              >
                <LogOut className="w-4 h-4" />
                <span className="hidden sm:block">Logout</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Tasks Section */}
          <div className="lg:col-span-2">
            <Tasks onTaskChange={refreshStats} />
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Quick Stats */}
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <LayoutDashboard className="w-5 h-5 text-blue-600" />
                Overview
              </h2>
              <div className="space-y-3">
                <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
                  <div className="flex items-center gap-2">
                    <CheckSquare2 className="w-5 h-5 text-blue-600" />
                    <span className="text-sm font-medium text-gray-700">Total Tasks</span>
                  </div>
                  <span className="text-2xl font-bold text-blue-600">
                    {statsLoading ? '...' : stats.totalTasks}
                  </span>
                </div>
                <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                  <div className="flex items-center gap-2">
                    <CheckCircle className="w-5 h-5 text-green-600" />
                    <span className="text-sm font-medium text-gray-700">Completed</span>
                  </div>
                  <span className="text-2xl font-bold text-green-600">
                    {statsLoading ? '...' : stats.completedTasks}
                  </span>
                </div>
                <div className="flex items-center justify-between p-3 bg-purple-50 rounded-lg">
                  <div className="flex items-center gap-2">
                    <FolderOpen className="w-5 h-5 text-purple-600" />
                    <span className="text-sm font-medium text-gray-700">Projects</span>
                  </div>
                  <span className="text-2xl font-bold text-purple-600">
                    {statsLoading ? '...' : stats.totalProjects}
                  </span>
                </div>
                <div className="flex items-center justify-between p-3 bg-orange-50 rounded-lg">
                  <div className="flex items-center gap-2">
                    <TagsIcon className="w-5 h-5 text-orange-600" />
                    <span className="text-sm font-medium text-gray-700">Tags</span>
                  </div>
                  <span className="text-2xl font-bold text-orange-600">
                    {statsLoading ? '...' : stats.totalTags}
                  </span>
                </div>
              </div>
            </div>

            {/* Projects Section */}
            <Projects onProjectChange={refreshStats} />

            {/* Tags Section */}
            <Tags onTagChange={refreshStats} />
          </div>
        </div>
      </main>
    </div>
  );
}