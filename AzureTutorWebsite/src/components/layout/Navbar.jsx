import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User, Bell } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <header className="bg-white border-b border-slate-200 h-16 flex items-center justify-between px-6 sticky top-0 z-30">
      <div className="flex items-center gap-2">
        <span className="text-xs font-black uppercase tracking-widest text-slate-400">Portal /</span>
        <span className="text-xs font-black uppercase tracking-widest text-primary-600">
          {user?.role === 'tutor' ? 'Tutor View' : 'Student View'}
        </span>
      </div>

      <div className="flex items-center gap-6">
        <button className="text-slate-400 hover:text-primary-600 transition-colors border-none bg-transparent cursor-pointer">
          <Bell className="w-5 h-5" />
        </button>
        
        <div className="h-8 w-[1px] bg-slate-100"></div>

        <div className="flex items-center gap-3">
          <div className="text-right hidden sm:block">
            <p className="text-xs font-black text-slate-900 leading-none">User {user?.user_id}</p>
            <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-1">{user?.email}</p>
          </div>
          <button 
            onClick={logout}
            className="w-10 h-10 rounded-xl bg-slate-50 flex items-center justify-center text-slate-500 hover:bg-red-50 hover:text-red-500 transition-all border-none cursor-pointer"
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
