import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { BookOpen, User, GraduationCap, AlertCircle, Loader2, Mail, Lock, CheckCircle } from 'lucide-react';

export default function Register() {
  const [role, setRole] = useState('Student');
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    grade: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [otpStep, setOtpStep] = useState(false);
  const [otp, setOtp] = useState('');
  const [verifying, setVerifying] = useState(false);
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    if (formData.password.length < 4) {
      return setError('Password must be at least 4 characters long.');
    }
    if (formData.password !== formData.confirmPassword) {
      return setError('Passwords do not match.');
    }
    
    setLoading(true);
    setError('');
    
    try {
      await api.post('/send_otp', { email: formData.email });
      setOtpStep(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to send verification email.');
    } finally {
      setLoading(false);
    }
  };

  const verifyAndRegister = async (e) => {
    e.preventDefault();
    setVerifying(true);
    setError('');

    try {
      await api.post('/verify_otp', { email: formData.email, otp });
      
      const payload = {
        name: formData.fullName,
        email: formData.email,
        phone: formData.phone,
        password: formData.password,
        role: role,
        ...(role === 'Student' && { grade_level: formData.grade })
      };
      
      await api.post('/register', payload);
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid OTP or registration failed.');
    } finally {
      setVerifying(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute top-0 -left-4 w-72 h-72 bg-primary-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30"></div>
      
      <div className="sm:mx-auto sm:w-full sm:max-w-md z-10 text-center mb-4">
        <div className="flex justify-center mb-6">
          <div className="w-16 h-16 bg-white rounded-2xl flex items-center justify-center shadow-xl">
            <BookOpen className="w-10 h-10 text-primary-600" />
          </div>
        </div>
        <h1 className="text-4xl font-black text-slate-900 tracking-tighter uppercase italic">Join Hub</h1>
        <p className="mt-1 text-sm text-slate-500 font-bold tracking-[0.2em] uppercase">Azure Cloud Market</p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md z-10 px-4">
        <div className="glass py-10 px-6 sm:px-10 rounded-3xl border border-white/40 shadow-2xl">
          {!otpStep ? (
            <>
              <div className="flex p-1.5 space-x-1 bg-slate-100/80 rounded-2xl mb-8">
                <button
                  type="button"
                  onClick={() => setRole('Student')}
                  className={`flex-1 flex justify-center items-center gap-2 py-3 text-xs font-black uppercase tracking-widest rounded-xl transition-all border-none cursor-pointer ${
                    role === 'Student' ? 'bg-white text-primary-600 shadow-md' : 'text-slate-500 hover:bg-white/50'
                  }`}
                >
                  <User className="w-4 h-4" /> Student
                </button>
                <button
                  type="button"
                  onClick={() => setRole('Tutor')}
                  className={`flex-1 flex justify-center items-center gap-2 py-3 text-xs font-black uppercase tracking-widest rounded-xl transition-all border-none cursor-pointer ${
                    role === 'Tutor' ? 'bg-white text-primary-600 shadow-md' : 'text-slate-500 hover:bg-white/50'
                  }`}
                >
                  <GraduationCap className="w-4 h-4" /> Tutor
                </button>
              </div>

              <form className="space-y-5" onSubmit={handleRegister}>
                {error && (
                  <div className="p-4 rounded-xl bg-red-50 border border-red-100 text-red-600 text-xs font-bold uppercase tracking-widest flex items-center gap-3">
                    <AlertCircle className="w-5 h-5" /> {error}
                  </div>
                )}

                <div className="space-y-1">
                  <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Full Name</label>
                  <input
                    type="text"
                    name="fullName"
                    required
                    className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:bg-white transition-all outline-none"
                    placeholder="John Doe"
                    value={formData.fullName}
                    onChange={handleInputChange}
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Email Address</label>
                  <input
                    type="email"
                    name="email"
                    required
                    className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:bg-white transition-all outline-none"
                    placeholder="name@example.com"
                    value={formData.email}
                    onChange={handleInputChange}
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Phone Number</label>
                  <input
                    type="tel"
                    name="phone"
                    required
                    className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:bg-white transition-all outline-none"
                    placeholder="9876543210"
                    value={formData.phone}
                    onChange={handleInputChange}
                  />
                </div>

                {role === 'Student' && (
                  <div className="space-y-1">
                    <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Grade Level</label>
                    <input
                      type="text"
                      name="grade"
                      required
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:bg-white transition-all outline-none"
                      placeholder="e.g. Class 10"
                      value={formData.grade}
                      onChange={handleInputChange}
                    />
                  </div>
                )}

                <div className="space-y-1">
                  <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Password</label>
                  <input
                    type="password"
                    name="password"
                    required
                    className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:bg-white transition-all outline-none"
                    placeholder="••••••••"
                    value={formData.password}
                    onChange={handleInputChange}
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Confirm Password</label>
                  <input
                    type="password"
                    name="confirmPassword"
                    required
                    className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:bg-white transition-all outline-none"
                    placeholder="••••••••"
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full py-5 vibrant-gradient text-white rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl active:scale-95 disabled:opacity-50 border-none cursor-pointer mt-4"
                >
                  {loading ? <Loader2 className="w-6 h-6 animate-spin mx-auto" /> : 'Send Verification Code'}
                </button>
              </form>
            </>
          ) : (
            <form className="space-y-6" onSubmit={verifyAndRegister}>
              <div className="text-center mb-8">
                <p className="text-slate-500 font-bold uppercase tracking-widest text-[10px] mb-2">Verification Required</p>
                <p className="text-sm font-bold text-slate-900">OTP sent to {formData.email}</p>
              </div>

              {error && (
                <div className="p-4 rounded-xl bg-red-50 border border-red-100 text-red-600 text-xs font-bold uppercase tracking-widest flex items-center gap-3">
                  <AlertCircle className="w-5 h-5" /> {error}
                </div>
              )}

              <div className="space-y-2">
                <label className="block text-center text-xs font-black text-slate-500 uppercase tracking-widest">Enter Code</label>
                <input
                  type="text"
                  required
                  maxLength="6"
                  className="w-full px-5 py-5 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:bg-white transition-all outline-none text-center font-black text-3xl tracking-[0.5em]"
                  placeholder="••••••"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                />
              </div>

              <div className="flex gap-4">
                <button
                  type="button"
                  onClick={() => setOtpStep(false)}
                  className="flex-1 py-5 bg-slate-100 text-slate-600 rounded-2xl font-black uppercase tracking-widest text-[10px] border-none cursor-pointer"
                >
                  Back
                </button>
                <button
                  type="submit"
                  disabled={verifying}
                  className="flex-[2] py-5 vibrant-gradient text-white rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl border-none cursor-pointer"
                >
                  {verifying ? <Loader2 className="w-6 h-6 animate-spin mx-auto" /> : 'Verify & Join Hub'}
                </button>
              </div>
            </form>
          )}

          <div className="mt-10 pt-10 border-t border-slate-100 text-center">
            <Link to="/login" className="text-xs font-black text-primary-600 uppercase tracking-widest no-underline hover:text-primary-700">
              Already familiar? Back to login
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
