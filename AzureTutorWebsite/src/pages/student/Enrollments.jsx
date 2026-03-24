import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { Calendar, Clock, Video, FileText, CheckCircle, BookCopy, AlertCircle } from 'lucide-react';

const parseCourseTime = (dateStr, timeStr) => {
  if(!dateStr || !timeStr) return new Date();
  
  let day, month, year;
  if(dateStr.includes('/')) {
    [day, month, year] = dateStr.split('/');
  } else if (dateStr.includes('-')) {
    const parts = dateStr.split('-');
    if(parts[0].length === 4) {
      year = parts[0]; month = parts[1]; day = parts[2];
    } else {
      year = parts[2]; month = parts[1]; day = parts[0];
    }
  }

  const [hours, minutes] = timeStr.split(':');
  return new Date(year, month - 1, day, hours, minutes);
};

export default function Enrollments() {
  const [courses, setCourses] = useState([]);
  const [historyCourses, setHistoryCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('active');
  const { user } = useAuth();
  const [now, setNow] = useState(new Date());

  useEffect(() => {
    if (user?.user_id) {
      fetchEnrollments();
    }
    const timer = setInterval(() => setNow(new Date()), 60000);
    return () => clearInterval(timer);
  }, [user?.user_id]);

  const fetchEnrollments = async () => {
    try {
      const [activeRes, historyRes] = await Promise.all([
        api.get(`/available_courses/${user.user_id}`),
        api.get(`/course_history/${user.user_id}`).catch(() => ({ data: { history: [] } }))
      ]);
      
      const allActive = activeRes.data.courses || [];
      const allHistory = historyRes.data.history || [];
      
      // Filter out truly expired courses from active list and move to conceptual history
      const nowTime = new Date();
      const actualActive = [];
      const extraHistory = [];
      
      allActive.forEach(course => {
        const startTime = parseCourseTime(course.start_date, course.start_time);
        const duration = parseInt(course.duration_minutes) || 60;
        const endTime = new Date(startTime.getTime() + duration * 60000);
        
        if (nowTime > endTime) {
          extraHistory.push(course);
        } else {
          actualActive.push(course);
        }
      });

      setCourses(actualActive);
      setHistoryCourses([...allHistory, ...extraHistory]);
    } catch (err) {
      setError('Failed to load your learning data.');
    } finally {
      setLoading(false);
    }
  };

  const isLive = (course) => {
    const startTime = parseCourseTime(course.start_date, course.start_time);
    const duration = parseInt(course.duration_minutes) || 60;
    const endTime = new Date(startTime.getTime() + duration * 60000);
    return now >= startTime && now <= endTime;
  };

  const isUpcoming = (course) => {
    const startTime = parseCourseTime(course.start_date, course.start_time);
    return now < startTime;
  };

  if (loading) return <div className="p-12 text-center text-slate-500 font-black uppercase tracking-[0.2em] text-[10px] animate-pulse">Synchronizing Learning Portal...</div>;

  const currentDisplay = activeTab === 'active' ? courses : historyCourses;

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="vibrant-gradient rounded-[2.5rem] p-10 text-white shadow-xl shadow-primary-600/20 relative overflow-hidden flex flex-col md:flex-row justify-between md:items-center">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
        <div className="relative z-10">
          <h1 className="text-4xl font-black tracking-tighter uppercase italic">My Learning</h1>
          <p className="text-white/80 font-bold tracking-[0.2em] uppercase text-xs mt-1">Azure Knowledge Sync</p>
        </div>
      </div>

      <div className="flex p-1.5 space-x-1 bg-slate-100/50 rounded-2xl mb-8 border border-white/60 shadow-sm max-w-sm">
        <button
          onClick={() => setActiveTab('active')}
          className={`flex-1 flex justify-center items-center gap-2 py-4 text-[10px] font-black uppercase tracking-[0.2em] rounded-xl transition-all duration-300 border-none cursor-pointer ${
            activeTab === 'active' ? 'vibrant-gradient text-white shadow-xl shadow-primary-500/20' : 'text-slate-500 hover:bg-white/50 bg-transparent'
          }`}
        >
          Active
        </button>
        <button
          onClick={() => setActiveTab('history')}
          className={`flex-1 flex justify-center items-center gap-2 py-4 text-[10px] font-black uppercase tracking-[0.2em] rounded-xl transition-all duration-300 border-none cursor-pointer ${
            activeTab === 'history' ? 'vibrant-gradient text-white shadow-xl shadow-primary-500/20' : 'text-slate-500 hover:bg-white/50 bg-transparent'
          }`}
        >
          History
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {currentDisplay.length === 0 && (
          <div className="col-span-full py-20 text-center glass rounded-3xl border-slate-200">
            <BookCopy className="w-12 h-12 mx-auto mb-4 text-slate-300" />
            <p className="text-lg font-bold text-slate-900 uppercase tracking-widest text-xs">No courses found in this category.</p>
          </div>
        )}
        
        {currentDisplay.map((course) => (
          <div key={course.id} className={`glass rounded-3xl p-8 shadow-sm border-slate-200/50 hover:shadow-2xl transition-all group ${activeTab === 'history' ? 'opacity-70 grayscale-[0.5]' : ''}`}>
             <span className="inline-block px-3 py-1 bg-primary-50 text-primary-600 rounded-full text-[10px] font-black uppercase tracking-widest mb-4">
               {course.subject}
             </span>
             <h3 className="text-2xl font-black text-slate-900 mb-6 group-hover:text-primary-600 transition-colors">
               {course.title.toUpperCase()}
             </h3>
            
            <div className="grid grid-cols-2 gap-6 mb-8 bg-slate-50/50 p-6 rounded-2xl border border-slate-100">
              <div className="flex items-center gap-3">
                <Calendar className="w-4 h-4 text-slate-400" />
                <span className="text-sm font-bold text-slate-600">{course.start_date}</span>
              </div>
              <div className="flex items-center gap-3">
                <Clock className="w-4 h-4 text-slate-400" />
                <span className="text-sm font-bold text-slate-600">{course.start_time}</span>
              </div>
            </div>

            {activeTab === 'history' ? (
              <div className="w-full flex justify-center items-center gap-3 py-4 bg-slate-200 text-slate-500 font-black tracking-widest uppercase text-[10px] rounded-2xl">
                 <CheckCircle className="w-4 h-4" /> Session Concluded
              </div>
            ) : isUpcoming(course) ? (
              <div className="w-full flex justify-center items-center gap-3 py-4 bg-slate-100 text-slate-400 font-black tracking-widest uppercase text-[10px] rounded-2xl border border-slate-200">
                 <Clock className="w-4 h-4" /> Starts @ {course.start_time}
              </div>
            ) : isLive(course) ? (
              <a
                href={course.meeting_url?.startsWith('http') ? course.meeting_url : `https://${course.meeting_url}`}
                target="_blank"
                rel="noopener noreferrer"
                className="w-full flex justify-center items-center gap-3 py-4 bg-slate-900 hover:bg-black text-white font-black tracking-widest uppercase text-[10px] rounded-2xl transition-all shadow-xl active:scale-95 no-underline hover:vibrant-gradient"
              >
                <Video className="w-4 h-4 animate-pulse" /> Join Live Room
              </a>
            ) : (
              <div className="w-full flex justify-center items-center gap-3 py-4 bg-red-50 text-red-500 font-black tracking-widest uppercase text-[10px] rounded-2xl border border-red-100">
                Session Expired
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
