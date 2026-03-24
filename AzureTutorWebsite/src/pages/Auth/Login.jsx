import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import api from '../../services/api';
import { GraduationCap, User, BookOpen, AlertCircle, Loader2 } from 'lucide-react';

export default function Login() {
  const [role, setRole] = useState('Student');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/login', { email, password });
      const { email: userEmail, role: userRole, user_id } = response.data;
      
      login({ email: userEmail, role: userRole, user_id });
      
      // Navigate based on the returned role or selected role
      const effectiveRole = (userRole || role).toLowerCase();
      const targetPath = effectiveRole === 'tutor' ? '/tutor/dashboard' : '/student/dashboard';
      navigate(targetPath);
      
    } catch (err) {
      setError(err.response?.data?.message || "Invalid credentials or server error.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-white flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute top-0 left-0 w-full h-[45vh] bg-indigo-600 shadow-2xl"></div>
      
      <div className="sm:mx-auto sm:w-full sm:max-w-md z-10 animate-fade-in text-center mb-10">
        <div className="flex justify-center mb-6">
          <div className="w-20 h-20 bg-white rounded-3xl flex items-center justify-center shadow-2xl ring-8 ring-white/10">
            <BookOpen className="w-12 h-12 text-indigo-600" />
          </div>
        </div>
        <h1 className="text-4xl font-black text-white tracking-tighter uppercase italic drop-shadow-md">
          Tutor Hub
        </h1>
        <p className="mt-1 text-sm text-white/80 font-bold tracking-[0.2em] uppercase drop-shadow-sm">
          Azure Cloud Market
        </p>
      </div>

      <div className="mt-2 sm:mx-auto sm:w-full sm:max-w-md z-10 animate-fade-in px-4">
        <div className="bg-white py-10 px-6 sm:px-10 rounded-[2.5rem] shadow-2xl border border-white/40">
          <div className="flex p-1.5 space-x-1 bg-slate-100/80 rounded-[1.5rem] mb-8">
            <button
              onClick={() => setRole('Student')}
              className={`flex-1 flex justify-center items-center gap-2 py-4 text-[10px] font-black uppercase tracking-[0.2em] rounded-2xl transition-all border-none cursor-pointer ${
                role === 'Student' ? 'bg-indigo-600 text-white shadow-xl scale-105' : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              <User className="w-4 h-4" />
              Student
            </button>
            <button
              onClick={() => setRole('Tutor')}
              className={`flex-1 flex justify-center items-center gap-2 py-4 text-[10px] font-black uppercase tracking-[0.2em] rounded-2xl transition-all border-none cursor-pointer ${
                role === 'Tutor' ? 'bg-indigo-600 text-white shadow-xl scale-105' : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              <GraduationCap className="w-4 h-4" />
              Tutor
            </button>
          </div>

          <form className="space-y-6" onSubmit={handleLogin}>
            {error && (
              <div className="p-4 rounded-xl bg-red-50 border border-red-100 text-red-600 text-sm flex items-center gap-3">
                <AlertCircle className="w-5 h-5" /> {error}
              </div>
            )}

            <div>
              <label className="block text-[10px] font-black text-slate-800 uppercase tracking-[0.15em] ml-1 mb-2 font-bold">Email Address</label>
              <input
                type="email"
                required
                className="block w-full px-5 py-4 bg-white border-2 border-slate-300 rounded-2xl focus:ring-4 focus:ring-indigo-500/20 focus:border-indigo-600 transition-all outline-none font-bold text-slate-900 shadow-sm"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div>
              <label className="block text-[10px] font-black text-slate-800 uppercase tracking-[0.15em] ml-1 mb-2 font-bold">Password</label>
              <input
                type="password"
                required
                className="block w-full px-5 py-4 bg-white border-2 border-slate-300 rounded-2xl focus:ring-4 focus:ring-indigo-500/20 focus:border-indigo-600 transition-all outline-none font-bold text-slate-900 shadow-sm"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full flex justify-center py-5 px-4 bg-indigo-600 hover:bg-indigo-700 rounded-2xl shadow-2xl shadow-indigo-600/30 text-sm font-black text-white uppercase tracking-[0.2em] hover:scale-[1.02] active:scale-[0.98] transition-all disabled:opacity-70 border-none cursor-pointer"
            >
              {loading ? <Loader2 className="w-6 h-6 animate-spin" /> : 'Sign In To Portal'}
            </button>
          </form>

          <div className="mt-8 pt-8 border-t border-slate-100 text-center">
            <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-4">New to Hub?</p>
            <Link to="/register" className="block w-full py-4 px-4 bg-slate-50 hover:bg-slate-100 text-slate-600 rounded-2xl text-xs font-black uppercase tracking-widest transition-all no-underline">
              Create An Account
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
