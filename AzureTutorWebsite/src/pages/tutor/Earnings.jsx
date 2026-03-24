import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { CreditCard, TrendingUp, AlertCircle, CheckCircle, Loader2 } from 'lucide-react';

export default function Earnings() {
  const [earnings, setEarnings] = useState({ total_earnings: 0, transactions: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    if (user?.user_id) {
      fetchEarnings();
    }
  }, [user?.user_id]);

  const fetchEarnings = async () => {
    try {
      const response = await api.get(`/tutor_earnings/${user.user_id}`);
      setEarnings(response.data);
    } catch (err) {
      setError('Failed to fetch your revenue details.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="p-12 text-center text-slate-500 font-medium italic">Calculating your success...</div>;

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
        <div className="vibrant-gradient rounded-[2.5rem] p-10 text-white shadow-xl shadow-primary-600/20 relative overflow-hidden flex-1 w-full md:w-auto">
          <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
          <h1 className="text-4xl font-black tracking-tighter uppercase italic">Revenue Center</h1>
          <p className="text-white/80 font-bold tracking-[0.2em] uppercase text-xs mt-1">Azure Educator Finance</p>
        </div>
        <div className="flex gap-4">
           <Link to="/tutor/bank-details" className="bg-slate-100 hover:bg-slate-200 text-slate-700 px-8 py-4 rounded-3xl font-bold transition-all border-none uppercase tracking-widest text-xs shadow-sm">
             Withdraw Settings
           </Link>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-100 text-red-700 rounded-2xl flex items-center gap-3 animate-fade-in font-medium">
          <AlertCircle className="w-5 h-5 shrink-0" /> {error}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1 space-y-8">
          <div className="vibrant-gradient rounded-[2rem] p-10 text-white shadow-xl shadow-primary-500/20 relative overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -mr-16 -mt-16 blur-2xl"></div>
            <p className="text-primary-100 font-bold uppercase tracking-widest text-xs mb-2">Total Balance</p>
            <h2 className="text-5xl font-black text-glow">₹{earnings.total_earnings || 0}</h2>
            <div className="mt-8 pt-8 border-t border-white/10 flex items-center gap-2 text-sm font-medium opacity-80">
              <CheckCircle className="w-4 h-4" /> Professional Tier Tutor
            </div>
          </div>

          <div className="glass rounded-[2rem] p-8 border-slate-200/50 shadow-sm">
            <h3 className="text-lg font-bold text-slate-900 mb-6">Withdrawal Method</h3>
            <div className="flex items-center gap-4 p-4 bg-slate-50 rounded-2xl border border-slate-100">
              <div className="w-12 h-12 bg-white rounded-xl flex items-center justify-center shadow-sm">
                <CreditCard className="w-6 h-6 text-primary-600" />
              </div>
              <div>
                <p className="text-sm font-bold text-slate-900">Direct Bank Transfer</p>
                <p className="text-xs text-slate-400 font-medium italic">Active & Verified</p>
              </div>
            </div>
          </div>
        </div>

        <div className="lg:col-span-2 glass rounded-[2rem] p-4 sm:p-8 border-slate-200/50 shadow-sm">
          <h3 className="text-xl font-extrabold text-slate-900 mb-8 px-2">Transaction History</h3>
          <div className="space-y-4">
            {(!earnings.transactions || earnings.transactions.length === 0) && (
              <div className="py-20 text-center text-slate-400 font-medium">
                No recent transactions found.
              </div>
            )}
            {earnings.transactions?.map((tx, idx) => (
              <div key={idx} className="flex items-center justify-between p-6 hover:bg-slate-50 rounded-2xl transition-all group border border-transparent hover:border-slate-100">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-primary-100 text-primary-600 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform">
                    <TrendingUp className="w-6 h-6" />
                  </div>
                  <div>
                    <p className="text-base font-bold text-slate-900">{tx.course_title || 'Course Payment'}</p>
                    <p className="text-xs text-slate-400 font-medium">Student ID: {tx.student_id}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-lg font-black text-emerald-600">+₹{tx.amount}</p>
                  <p className="text-[10px] text-slate-300 font-bold uppercase tracking-tighter">{tx.timestamp || 'Recent'}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
