import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { Landmark, CreditCard, User, Building, ShieldCheck, Loader2 } from 'lucide-react';

export default function BankDetails() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [hasExisting, setHasExisting] = useState(false);
  const [details, setDetails] = useState({
    account_holder_name: '',
    account_number: '',
    ifsc_code: '',
    bank_name: '',
    upi_id: ''
  });

  useEffect(() => {
    if (user?.user_id) fetchDetails();
  }, [user?.user_id]);

  const fetchDetails = async () => {
    try {
      const res = await api.get(`/get_bank_details/${user.user_id}`);
      if (res.data && res.data.account_number) {
        setDetails(res.data);
        setHasExisting(true);
      }
    } catch (err) {
      console.log("No existing bank details found.");
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');
    setSuccess('');
    try {
      const endpoint = hasExisting ? '/save_bank_details' : '/add_bank_details';
      await api.post(endpoint, { ...details, tutor_id: user.user_id });
      setSuccess('Payout details encrypted and saved successfully.');
      setHasExisting(true);
    } catch (err) {
      setError('Failed to securely save details. Please verify your info.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="p-12 text-center text-slate-500 font-black uppercase tracking-[0.2em] text-[10px] animate-pulse">Accessing Secure Vault...</div>;

  return (
    <div className="max-w-4xl mx-auto space-y-10 animate-fade-in">
      <div className="vibrant-gradient rounded-[2.5rem] p-10 text-white shadow-xl shadow-primary-600/20 relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
        <h1 className="text-4xl font-black tracking-tighter uppercase italic">Payout Portal</h1>
        <p className="text-white/80 font-bold tracking-[0.2em] uppercase text-xs mt-1">Azure Financial Sync</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2">
          <form onSubmit={handleSave} className="glass rounded-[2.5rem] p-10 border-slate-200/50 space-y-8">
            <div className="flex items-center gap-4 mb-4">
              <ShieldCheck className="w-6 h-6 text-primary-600" />
              <h2 className="text-xl font-black text-slate-900 uppercase tracking-widest text-xs">Secure Payout Setup</h2>
            </div>

            {error && <div className="p-4 bg-red-50 text-red-600 rounded-2xl text-xs font-bold uppercase tracking-widest">{error}</div>}
            {success && <div className="p-4 bg-green-50 text-green-700 rounded-2xl text-xs font-black uppercase tracking-widest">{success}</div>}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">Account Holder</label>
                <input
                  type="text"
                  required
                  className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:bg-white focus:border-primary-500 transition-all font-bold text-sm"
                  value={details.account_holder_name}
                  onChange={(e) => setDetails({...details, account_holder_name: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">Bank Name</label>
                <input
                  type="text"
                  required
                  className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:bg-white focus:border-primary-500 transition-all font-bold text-sm"
                  value={details.bank_name}
                  onChange={(e) => setDetails({...details, bank_name: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">Account Number</label>
                <input
                  type="text"
                  required
                  className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:bg-white focus:border-primary-500 transition-all font-bold text-sm"
                  value={details.account_number}
                  onChange={(e) => setDetails({...details, account_number: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">IFSC Code</label>
                <input
                  type="text"
                  required
                  className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:bg-white focus:border-primary-500 transition-all font-bold text-sm"
                  value={details.ifsc_code}
                  onChange={(e) => setDetails({...details, ifsc_code: e.target.value})}
                />
              </div>
              <div className="space-y-2 md:col-span-2">
                <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">UPI ID (Optional)</label>
                <input
                  type="text"
                  className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:bg-white focus:border-primary-500 transition-all font-bold text-sm"
                  placeholder="e.g. name@upi"
                  value={details.upi_id}
                  onChange={(e) => setDetails({...details, upi_id: e.target.value})}
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={saving}
              className="w-full py-5 vibrant-gradient text-white rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl active:scale-95 disabled:opacity-50 border-none cursor-pointer"
            >
              {saving ? <Loader2 className="w-6 h-6 animate-spin mx-auto" /> : 'Authorize Payout Settings'}
            </button>
          </form>
        </div>

        <div className="space-y-6">
          <div className="glass rounded-[2rem] p-8 border-slate-100">
             <Landmark className="w-8 h-8 text-primary-600 mb-4" />
             <h3 className="font-black text-xs uppercase tracking-widest text-slate-900 mb-2">Automated Payouts</h3>
             <p className="text-xs text-slate-500 font-bold leading-relaxed">Your earnings are processed daily and transferred to your link account minus platform synchronization fees.</p>
          </div>
          <div className="glass rounded-[2rem] p-8 border-slate-100">
             <CreditCard className="w-8 h-8 text-slate-400 mb-4" />
             <h3 className="font-black text-xs uppercase tracking-widest text-slate-900 mb-2">Primary Method</h3>
             <p className="text-xs text-slate-500 font-bold">Standard Bank Transfer is your active withdrawal method.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
