import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { 
  BookOpen, PlusCircle, Trash2, Users, Loader2, X, 
  Calendar, Clock, FileText, Video, AlertCircle, CheckCircle, Book
} from 'lucide-react';

export default function ManageCourses() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [newCourse, setNewCourse] = useState({
    title: '', subject: '', price: '', max_students: '', 
    duration_minutes: '', start_date: '', start_time: '', 
    meeting_url: '', professor_message: '', curriculum: '',
    class_type: 'Online', frequency: 'Weekly'
  });

  useEffect(() => {
    if (user?.email) {
      fetchCourses();
    }
  }, [user?.email]);

  const fetchCourses = async () => {
    try {
      const response = await api.get('/get_all_courses');
      const myCourses = (response.data.courses || []).filter(c => c.tutor_email === user?.email);
      setCourses(myCourses);
    } catch (err) {
      setError('Failed to fetch courses.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (courseId) => {
    if (!window.confirm('Are you sure you want to delete this course? This action is irreversible.')) return;
    try {
      await api.delete(`/delete_course/${courseId}`);
      setCourses(courses.filter(c => c.id !== courseId));
      setSuccess('Course deleted successfully.');
    } catch (err) {
      setError('Failed to delete course.');
    }
  };

  const handleCreateCourse = async (e) => {
    e.preventDefault();
    setCreating(true);
    setError('');
    setSuccess('');
    
    let formattedDate = newCourse.start_date;
    try {
      if (newCourse.start_date) {
        const parts = newCourse.start_date.split('-');
        if (parts.length === 3) {
          formattedDate = `${parts[2]}/${parts[1]}/${parts[0]}`; // DD/MM/YYYY
        }
      }
    } catch (e) {}
    
    const courseData = {
      user_id: user?.user_id,
      title: newCourse.title,
      subject: newCourse.subject,
      price: newCourse.price,
      date: formattedDate,
      time: newCourse.start_time,
      meeting_url: newCourse.meeting_url,
      curriculum: newCourse.curriculum || '',
      professor_message: newCourse.professor_message || '',
      tutor_email: user?.email,
      duration: parseInt(newCourse.duration_minutes) || 60,
      class_type: newCourse.class_type,
      frequency: newCourse.frequency,
      max_students: parseInt(newCourse.max_students) || 50
    };

    try {
      await api.post('/create_course', courseData);
      setShowModal(false);
      setNewCourse({
        title: '', subject: '', price: '', max_students: '', 
        duration_minutes: '', start_date: '', start_time: '', 
        meeting_url: '', professor_message: '', curriculum: '',
        class_type: 'Online', frequency: 'Weekly'
      });
      setSuccess('Course created successfully!');
      fetchCourses();
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Failed to create course. Please check all fields.');
    } finally {
      setCreating(false);
    }
  };

  if (loading) return <div className="p-12 text-center text-slate-500 font-medium italic">Synchronizing your curriculum...</div>;

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
        <div className="vibrant-gradient rounded-[2.5rem] p-10 text-white shadow-xl shadow-primary-600/20 relative overflow-hidden flex-1 w-full md:w-auto">
          <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
          <h1 className="text-4xl font-black tracking-tighter uppercase italic">Course Inventory</h1>
          <p className="text-white/80 font-bold tracking-[0.2em] uppercase text-xs mt-1">Azure Educator Market</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="vibrant-gradient text-white px-8 py-4 rounded-3xl font-bold flex items-center gap-3 shadow-lg shadow-primary-500/30 hover:scale-[1.05] active:scale-95 transition-all text-lg cursor-pointer border-none whitespace-nowrap"
        >
          <PlusCircle className="w-6 h-6" />
          Create New Course
        </button>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-100 text-red-700 rounded-2xl flex items-center gap-3 animate-fade-in font-medium">
          <AlertCircle className="w-5 h-5 shrink-0" /> {error}
        </div>
      )}
      {success && (
        <div className="p-4 bg-green-50 border border-green-100 text-green-700 rounded-2xl flex items-center gap-3 animate-fade-in font-bold">
          <CheckCircle className="w-5 h-5 shrink-0" /> {success}
        </div>
      )}

      <div className="glass rounded-3xl overflow-hidden border-slate-200/50 shadow-sm">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-100">
            <thead className="bg-slate-50/50">
              <tr>
                <th className="px-8 py-6 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Course Title</th>
                <th className="px-8 py-6 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Subject</th>
                <th className="px-8 py-6 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Price</th>
                <th className="px-8 py-6 text-left text-xs font-bold text-slate-400 uppercase tracking-widest">Students</th>
                <th className="px-8 py-6 text-right text-xs font-bold text-slate-400 uppercase tracking-widest whitespace-nowrap">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-transparent divide-y divide-slate-100">
              {courses.length === 0 && (
                <tr>
                  <td colSpan="5" className="px-8 py-20 text-center text-slate-500 italic">
                    <Book className="w-12 h-12 mx-auto mb-4 text-slate-200" />
                    No courses created yet. Start your journey by creating one!
                  </td>
                </tr>
              )}
              {courses.map((course) => (
                <tr key={course.id} className="hover:bg-slate-50/80 transition-colors group">
                  <td className="px-8 py-6 whitespace-nowrap">
                    <div className="text-base font-bold text-slate-900 group-hover:text-primary-600 transition-colors">{course.title}</div>
                    <div className="text-xs text-slate-400 font-medium">Starts {course.start_date} @ {course.start_time}</div>
                  </td>
                  <td className="px-8 py-6 whitespace-nowrap text-sm font-bold text-slate-500">{course.subject}</td>
                  <td className="px-8 py-6 whitespace-nowrap text-sm">
                    <span className="font-extrabold text-slate-900">₹{course.price}</span>
                  </td>
                  <td className="px-8 py-6 whitespace-nowrap text-sm">
                    <div className="flex items-center gap-2">
                       <span className="font-bold text-slate-900">{course.enrolled_count}</span>
                       <span className="text-slate-300">/</span>
                       <span className="text-slate-400">{course.max_students}</span>
                    </div>
                  </td>
                  <td className="px-8 py-6 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      onClick={() => handleDelete(course.id)}
                      className="inline-flex items-center gap-1.5 text-red-500 hover:text-red-700 hover:bg-red-50 px-4 py-2 rounded-xl bg-transparent transition-all font-bold cursor-pointer border-none"
                    >
                      <Trash2 className="w-4 h-4" />
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
          <div className="glass w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-[2rem] shadow-2xl border-white/40">
            <div className="p-8 space-y-8">
              <div className="flex justify-between items-center border-b border-slate-100 pb-6">
                <h2 className="text-2xl font-extrabold text-slate-900 flex items-center gap-3">
                  <PlusCircle className="w-7 h-7 text-primary-600" />
                  Create Premium Course
                </h2>
                <button 
                  onClick={() => setShowModal(false)}
                  className="p-2 hover:bg-slate-100 rounded-xl transition-colors cursor-pointer border-none"
                >
                  <X className="w-6 h-6 text-slate-400" />
                </button>
              </div>

              <form onSubmit={handleCreateCourse} className="space-y-8">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div className="space-y-6 md:col-span-2">
                    <div className="space-y-2">
                      <label className="text-sm font-bold text-slate-700 ml-1">Course Title</label>
                      <input
                        required
                        className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all text-slate-900 outline-none"
                        placeholder="e.g., Advanced React Mastery"
                        value={newCourse.title}
                        onChange={(e) => setNewCourse({...newCourse, title: e.target.value})}
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Subject Area</label>
                    <input
                      required
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      placeholder="e.g., Web Development"
                      value={newCourse.subject}
                      onChange={(e) => setNewCourse({...newCourse, subject: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Price (₹)</label>
                    <input
                      required
                      type="number"
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      placeholder="0.00"
                      value={newCourse.price}
                      onChange={(e) => setNewCourse({...newCourse, price: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Capacity</label>
                    <input
                      required
                      type="number"
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      placeholder="Max students"
                      value={newCourse.max_students}
                      onChange={(e) => setNewCourse({...newCourse, max_students: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Duration (Min)</label>
                    <input
                      required
                      type="number"
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      placeholder="e.g., 60"
                      value={newCourse.duration_minutes}
                      onChange={(e) => setNewCourse({...newCourse, duration_minutes: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Start Date</label>
                    <input
                      required
                      type="date"
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      value={newCourse.start_date}
                      onChange={(e) => setNewCourse({...newCourse, start_date: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Session Time</label>
                    <input
                      required
                      type="time"
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      value={newCourse.start_time}
                      onChange={(e) => setNewCourse({...newCourse, start_time: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Class Type</label>
                    <select
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      value={newCourse.class_type}
                      onChange={(e) => setNewCourse({...newCourse, class_type: e.target.value})}
                    >
                      <option value="Online">Online</option>
                      <option value="In-person">In-person</option>
                    </select>
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Frequency</label>
                    <select
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      value={newCourse.frequency}
                      onChange={(e) => setNewCourse({...newCourse, frequency: e.target.value})}
                    >
                      <option value="Daily">Daily</option>
                      <option value="Weekly">Weekly</option>
                      <option value="Monthly">Monthly</option>
                      <option value="One-time">One-time</option>
                    </select>
                  </div>

                  <div className="space-y-2 md:col-span-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Live Classroom Link</label>
                    <input
                      required
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all outline-none"
                      placeholder="Zoom / Teams / Google Meet link"
                      value={newCourse.meeting_url}
                      onChange={(e) => setNewCourse({...newCourse, meeting_url: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2 md:col-span-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Curriculum Details</label>
                    <textarea
                      required
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all min-h-[120px] outline-none"
                      placeholder="List the topics covered in this course..."
                      value={newCourse.curriculum}
                      onChange={(e) => setNewCourse({...newCourse, curriculum: e.target.value})}
                    />
                  </div>

                  <div className="space-y-2 md:col-span-2">
                    <label className="text-sm font-bold text-slate-700 ml-1">Welcome Message</label>
                    <textarea
                      className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all min-h-[120px] outline-none"
                      placeholder="Welcome your students to the classroom..."
                      value={newCourse.professor_message}
                      onChange={(e) => setNewCourse({...newCourse, professor_message: e.target.value})}
                    />
                  </div>
                </div>

                <div className="flex gap-4 pt-4">
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="flex-1 py-4 px-6 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-2xl transition-all cursor-pointer border-none"
                  >
                    Discard
                  </button>
                  <button
                    type="submit"
                    disabled={creating}
                    className="flex-1 py-4 px-6 vibrant-gradient text-white font-bold rounded-2xl transition-all shadow-xl shadow-primary-500/20 active:scale-95 disabled:opacity-50 cursor-pointer border-none"
                  >
                    {creating ? <Loader2 className="w-6 h-6 animate-spin mx-auto text-white" /> : 'Launch Course'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
