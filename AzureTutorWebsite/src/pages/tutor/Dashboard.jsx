import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Link } from 'react-router-dom';
import { LayoutDashboard, FileText, CreditCard, Loader2 } from 'lucide-react';
import api from '../../services/api';

export default function TutorDashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState({ courses: 0, earnings: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      if (!user?.user_id) return;
      try {
        const [coursesRes, earningsRes] = await Promise.all([
          api.get('/get_all_courses'),
          api.get(`/tutor_earnings/${user.user_id}`)
        ]);
        
        const myCoursesCount = (coursesRes.data.courses || []).filter(c => c.tutor_email === user.email).length;
        
        setStats({
          courses: myCoursesCount,
          earnings: earningsRes.data.total_earnings || 0
        });
      } catch (err) {
        console.error("Failed to fetch tutor dashboard stats", err);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, [user?.user_id, user?.email]);

  if (!user) return <div className="p-8 text-center text-slate-500">Redirecting...</div>;

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="vibrant-gradient rounded-[2.5rem] p-10 text-white shadow-xl shadow-primary-600/20 relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
        <h1 className="text-4xl font-black tracking-tighter uppercase italic">Tutor Portal</h1>
        <p className="text-white/80 font-bold tracking-[0.2em] uppercase text-xs mt-1">Azure Educator Cloud</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="glass rounded-3xl p-8 flex flex-col hover:shadow-2xl transition-all duration-300 group ring-1 ring-black/5">
          <div className="flex justify-between items-start mb-6">
            <div className="w-14 h-14 bg-blue-100 text-blue-600 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
              <FileText className="w-7 h-7" />
            </div>
            <div className="text-right">
              <span className="text-3xl font-extrabold text-slate-900 block tracking-tight">
                {loading ? <Loader2 className="w-6 h-6 animate-spin inline-block text-blue-500" /> : stats.courses}
              </span>
              <p className="text-xs text-slate-400 font-bold uppercase tracking-wider">Active Courses</p>
            </div>
          </div>
          <h2 className="text-2xl font-bold text-slate-900 mb-3">Manage Courses</h2>
          <p className="text-slate-500 mb-8 flex-1 text-base leading-relaxed">
            Create, edit, and organize your curriculum. Keep track of students and session schedules.
          </p>
          <Link
            to="/tutor/courses"
            className="inline-flex items-center justify-center py-4 px-6 bg-slate-900 hover:bg-slate-800 text-white font-bold rounded-2xl transition-all shadow-lg active:scale-95"
          >
            Manage Dashboard
          </Link>
        </div>

        <div className="glass rounded-3xl p-8 flex flex-col hover:shadow-2xl transition-all duration-300 group ring-1 ring-black/5">
          <div className="flex justify-between items-start mb-6">
            <div className="w-14 h-14 bg-emerald-100 text-emerald-600 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
              <CreditCard className="w-7 h-7" />
            </div>
            <div className="text-right">
              <span className="text-3xl font-extrabold text-slate-900 block tracking-tight">
                {loading ? <Loader2 className="w-6 h-6 animate-spin inline-block text-emerald-500" /> : `₹${stats.earnings}`}
              </span>
              <p className="text-xs text-slate-400 font-bold uppercase tracking-wider">Total Revenue</p>
            </div>
          </div>
          <h2 className="text-2xl font-bold text-slate-900 mb-3">My Earnings</h2>
          <p className="text-slate-500 mb-8 flex-1 text-base leading-relaxed">
            Monitor your financial performance and manage your payout settings securely.
          </p>
          <Link
            to="/tutor/earnings"
            className="inline-flex items-center justify-center py-4 px-6 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-2xl transition-all shadow-lg active:scale-95 shadow-emerald-500/20"
          >
            View Revenue Analytics
          </Link>
        </div>
      </div>
    </div>
  );
}
