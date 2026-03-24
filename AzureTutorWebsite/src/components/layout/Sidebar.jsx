import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { 
  LayoutDashboard, 
  BookOpen, 
  LogOut, 
  Search, 
  GraduationCap, 
  IndianRupee, 
  CreditCard 
} from 'lucide-react';

const Sidebar = () => {
  const { user, logout } = useAuth();
  const isTutor = user?.role === 'tutor';

  const menuItems = isTutor ? [
    { name: 'Dashboard', path: '/tutor/dashboard', icon: LayoutDashboard },
    { name: 'My Courses', path: '/tutor/courses', icon: BookOpen },
    { name: 'Earnings', path: '/tutor/earnings', icon: IndianRupee },
    { name: 'Bank Details', path: '/tutor/bank-details', icon: CreditCard },
  ] : [
    { name: 'Dashboard', path: '/student/dashboard', icon: LayoutDashboard },
    { name: 'Explore', path: '/student/explore', icon: Search },
    { name: 'My Learning', path: '/student/enrollments', icon: GraduationCap },
  ];

  return (
    <aside className="w-72 bg-white border-r border-slate-200 flex flex-col relative z-40">
      <div className="p-8">
        <div className="flex items-center gap-3 mb-2">
          <div className="w-10 h-10 vibrant-gradient rounded-xl flex items-center justify-center text-white shadow-lg">
            <BookOpen className="w-6 h-6" />
          </div>
          <span className="text-xl font-black text-slate-900 tracking-tighter uppercase italic">Azure Hub</span>
        </div>
        <p className="text-[10px] font-black text-slate-400 tracking-[0.2em] uppercase ml-1">Learning Ecosystem</p>
      </div>

      <nav className="flex-1 px-4 space-y-1 mt-4">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `
              flex items-center gap-4 px-5 py-4 rounded-2xl text-sm font-black uppercase tracking-widest transition-all no-underline
              ${isActive 
                ? 'bg-primary-50 text-primary-600 shadow-sm border border-primary-100/50' 
                : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900'}
            `}
          >
            <item.icon className="w-5 h-5 flex-shrink-0" />
            {item.name}
          </NavLink>
        ))}
      </nav>

      <div className="p-6 border-t border-slate-100">
        <button
          onClick={logout}
          className="w-full flex items-center gap-4 px-5 py-4 rounded-2xl text-sm font-black uppercase tracking-widest bg-red-50 text-red-500 hover:bg-red-500 hover:text-white transition-all border-none cursor-pointer"
        >
          <LogOut className="w-5 h-5" />
          Sign Out
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
