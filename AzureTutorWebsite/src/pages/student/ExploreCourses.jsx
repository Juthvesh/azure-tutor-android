import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { Calendar, Clock, DollarSign, Users, CheckCircle } from 'lucide-react';

export default function ExploreCourses() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [enrolling, setEnrolling] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    if (user?.user_id) {
      fetchCourses();
    }
  }, [user?.user_id]);

  const fetchCourses = async () => {
    try {
      const response = await api.get(`/explore_courses/${user.user_id}`);
      setCourses(response.data.courses || []);
    } catch (err) {
      setError('Failed to load available courses.');
    } finally {
      setLoading(false);
    }
  };

  const handleEnrollAndPay = async (courseId) => {
    if (!user?.user_id) return alert('Please login to enroll.');
    const course = courses.find(c => c.id === courseId);
    if (!course) return;

    setEnrolling(courseId);
    setError('');
    setSuccess('');
    
    try {
      // 1. Create payment order
      const orderRes = await api.post('/create_payment_order', {
        amount: course.price,
        student_id: user.user_id,
        course_id: courseId
      });

      const options = {
        key: "rzp_test_SNrDZRnmpJrON9", // Test ID from backend
        amount: orderRes.data.amount * 100, // In paise
        currency: "INR",
        name: "AzureTutor",
        description: `Enrollment for ${course.title}`,
        order_id: orderRes.data.order_id,
        handler: async function (response) {
          try {
            // 2. Perform enrollment
            await api.post('/enroll_student', { 
              student_id: user.user_id, 
              course_id: courseId 
            });
            
            // 3. Verify payment 
            await api.post('/verify_payment', { 
              student_id: user.user_id, 
              course_id: courseId,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_order_id: response.razorpay_order_id,
              razorpay_signature: response.razorpay_signature
            });
            
            setSuccess(`Successfully enrolled in ${course.title}!`);
            setTimeout(() => {
              setCourses(courses.filter((c) => c.id !== courseId));
            }, 1500);
          } catch (err) {
            console.error("Payment verification failed:", err);
            setError('Payment verification failed. Please contact support.');
          }
        },
        prefill: {
          email: user.email
        },
        theme: {
          color: "#6366F1"
        }
      };

      const rzp1 = new window.Razorpay(options);
      rzp1.open();

    } catch (err) {
      console.error("Enrollment error:", err.response?.data || err);
      setError(err.response?.data?.error || err.response?.data?.message || 'Failed to initiate payment.');
    } finally {
      setEnrolling(null);
    }
  };

  if (loading) return <div className="p-12 text-center text-slate-500 font-medium">Loading premium courses for you...</div>;

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="vibrant-gradient rounded-[2.5rem] p-10 text-white shadow-xl shadow-primary-600/20 relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
        <h1 className="text-4xl font-black tracking-tighter uppercase italic">Discovery Hub</h1>
        <p className="text-white/80 font-bold tracking-[0.2em] uppercase text-xs mt-1">Azure Cloud Market</p>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-100 text-red-700 rounded-2xl flex items-center gap-3 animate-fade-in font-bold uppercase tracking-widest text-[11px]">
          <AlertCircle className="w-5 h-5 shrink-0" /> {error}
        </div>
      )}
      {success && (
        <div className="p-4 bg-green-50 border border-green-100 text-green-700 rounded-2xl flex items-center gap-3 animate-fade-in font-black uppercase tracking-widest text-[11px]">
          <CheckCircle className="w-6 h-6 shrink-0"/> {success}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {courses.length === 0 && !loading && (
          <div className="col-span-full py-20 text-center text-slate-500 glass rounded-3xl border-slate-200">
            <Compass className="w-12 h-12 mx-auto mb-4 text-slate-300" />
            <p className="text-lg font-bold">No new courses available right now.</p>
            <p className="text-sm">Check back later for fresh content!</p>
          </div>
        )}
        {courses.map((course) => (
          <div key={course.id} className="glass rounded-3xl p-6 shadow-sm hover:shadow-2xl transition-all duration-300 flex flex-col h-full group border-slate-200/50">
            <div className="flex justify-between items-start mb-6">
              <div className="flex-1">
                <span className="inline-block px-3 py-1 bg-primary-50 text-primary-600 rounded-full text-[10px] font-black uppercase tracking-[0.2em] mb-2">
                  {course.subject}
                </span>
                <h3 className="text-xl font-black text-slate-900 group-hover:text-primary-600 transition-colors leading-tight uppercase tracking-tight">
                  {course.title}
                </h3>
              </div>
              <div className="vibrant-gradient text-white px-4 py-2 rounded-2xl text-xs font-black shadow-xl shadow-primary-500/20 whitespace-nowrap ml-2">
                ₹{course.price}
              </div>
            </div>
            
            <div className="space-y-3 mb-8 flex-1">
              <div className="flex items-center text-sm font-medium text-slate-500 gap-3">
                <div className="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center">
                  <Calendar className="w-4 h-4 text-slate-400" />
                </div>
                {course.start_date}
              </div>
              <div className="flex items-center text-sm font-medium text-slate-500 gap-3">
                <div className="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center">
                  <Clock className="w-4 h-4 text-slate-400" />
                </div>
                {course.start_time} ({course.duration_minutes} mins)
              </div>
              <div className="flex items-center text-sm font-medium text-slate-500 gap-3">
                <div className="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center">
                  <Users className="w-4 h-4 text-slate-400" />
                </div>
                {course.enrolled_count} / {course.max_students} Students enrolled
              </div>
            </div>

            <button
              onClick={() => handleEnrollAndPay(course.id)}
              disabled={enrolling === course.id || course.enrolled_count >= course.max_students}
              className="w-full mt-auto py-4 px-6 bg-slate-900 hover:bg-black text-white font-bold rounded-2xl transition-all shadow-xl active:scale-95 disabled:opacity-30 flex items-center justify-center gap-2 group-hover:vibrant-gradient group-hover:border-transparent cursor-pointer"
            >
              {enrolling === course.id ? (
                <Loader2 className="w-5 h-5 animate-spin" />
              ) : (
                course.enrolled_count >= course.max_students ? 'Class is Full' : 'Enroll & Pay Now'
              )}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
