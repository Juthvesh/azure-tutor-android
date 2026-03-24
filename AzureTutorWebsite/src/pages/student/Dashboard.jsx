import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { BookOpen, Award, Users, Clock, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function StudentDashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState({ enrollments: 0 });

  useEffect(() => {
    if (user?.user_id) {
      api.get(`/available_courses/${user.user_id}`).then(res => {
        setStats({ enrollments: res.data.courses?.length || 0 });
      });
    }
  }, [user?.user_id]);

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="vibrant-gradient rounded-[2.5rem] p-10 text-white shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
        <h1 className="text-4xl font-black tracking-tighter uppercase italic">Student Portal</h1>
        <p className="text-white/80 font-bold tracking-[0.2em] uppercase text-xs mt-1">Welcome back, User {user?.user_id}</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div className="glass p-8 rounded-[2rem] border-slate-200 flex items-center gap-6">
          <div className="w-16 h-16 rounded-2xl bg-primary-100 flex items-center justify-center text-primary-600">
            <BookOpen className="w-8 h-8" />
          </div>
          <div>
            <p className="text-2xl font-black text-slate-900">{stats.enrollments}</p>
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-1">Active Courses</p>
          </div>
        </div>
        
        <div className="glass p-8 rounded-[2rem] border-slate-200 flex items-center gap-6">
          <div className="w-16 h-16 rounded-2xl bg-orange-100 flex items-center justify-center text-orange-600">
            <Award className="w-8 h-8" />
          </div>
          <div>
            <p className="text-2xl font-black text-slate-900">Premium</p>
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-1">Account Level</p>
          </div>
        </div>

        <div className="glass p-8 rounded-[2rem] border-slate-200 flex items-center gap-6">
          <div className="w-16 h-16 rounded-2xl bg-green-100 flex items-center justify-center text-green-600">
            <Clock className="w-8 h-8" />
          </div>
          <div>
            <p className="text-2xl font-black text-slate-900">Synchronized</p>
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-1">App Connection</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="glass p-10 rounded-[2.5rem] border-slate-200 flex flex-col justify-between group">
          <div>
            <h3 className="text-xl font-black text-slate-900 uppercase tracking-widest text-xs mb-4">Start Learning</h3>
            <p className="text-slate-500 font-bold text-sm leading-relaxed mb-8">Access premium courses and live classrooms prepared by certified tutors across the hub.</p>
          </div>
          <Link to="/student/explore" className="w-fit flex items-center gap-3 px-8 py-4 vibrant-gradient text-white rounded-2xl font-black uppercase tracking-widest text-[10px] no-underline shadow-lg transform group-hover:translate-x-2 transition-all">
            Join Classes <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        <div className="glass p-10 rounded-[2.5rem] border-slate-200 flex flex-col justify-between group">
          <div>
            <h3 className="text-xl font-black text-slate-900 uppercase tracking-widest text-xs mb-4">My Schedule</h3>
            <p className="text-slate-500 font-bold text-sm leading-relaxed mb-8">View your enrolled classes, past sessions, and tracking timings of your upcoming learning sessions.</p>
          </div>
          <Link to="/student/enrollments" className="w-fit flex items-center gap-3 px-8 py-4 bg-slate-900 hover:bg-black text-white rounded-2xl font-black uppercase tracking-widest text-[10px] no-underline shadow-lg transform group-hover:translate-x-2 transition-all">
            View Schedule <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      </div>
    </div>
  );
}
