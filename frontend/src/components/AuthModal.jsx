import React, { useState } from 'react';
import { X, LogIn, UserPlus, Building, User } from 'lucide-react';
import { api, setToken, setUser } from '../api';

export default function AuthModal({ isOpen, initialMode = 'login', onClose, onAuthSuccess }) {
  const [mode, setMode] = useState(initialMode); // 'login' or 'register'
  const [role, setRole] = useState('ROLE_CUSTOMER'); // 'ROLE_CUSTOMER' or 'ROLE_STAFF'
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Form fields
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [businessName, setBusinessName] = useState('');
  const [category, setCategory] = useState('Clinics & Health');
  const [city, setCity] = useState('');
  const [address, setAddress] = useState('');

  if (!isOpen) return null;

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (mode === 'login') {
        const res = await api.login({ email, password });
        setToken(res.token);
        setUser(res);
        onAuthSuccess(res);
        onClose();
      } else {
        const req = {
          name,
          email,
          password,
          phone,
          role,
          businessName: role === 'ROLE_STAFF' ? businessName : null,
          category: role === 'ROLE_STAFF' ? category : null,
          city: role === 'ROLE_STAFF' ? city : null,
          address: role === 'ROLE_STAFF' ? address : null,
        };
        const res = await api.register(req);
        setToken(res.token);
        setUser(res);
        onAuthSuccess(res);
        onClose();
      }
    } catch (err) {
      setError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '480px' }}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            {mode === 'login' ? <LogIn size={20} style={{ color: 'var(--primary)' }} /> : <UserPlus size={20} style={{ color: 'var(--primary)' }} />}
            <h3 style={{ fontSize: '1.2rem', color: 'var(--text-main)', margin: 0 }}>
              {mode === 'login' ? 'Sign In to QueueEase' : 'Create an Account'}
            </h3>
          </div>
          <button className="modal-close-btn" onClick={onClose}>
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && (
              <div className="alert-box danger">
                <span>{error}</span>
              </div>
            )}

            {mode === 'register' && (
              <div style={{ marginBottom: '16px' }}>
                <label className="form-label">I want to join as:</label>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
                  <button
                    type="button"
                    className={`btn ${role === 'ROLE_CUSTOMER' ? 'btn-primary' : 'btn-secondary'}`}
                    style={{ fontSize: '0.85rem', padding: '8px' }}
                    onClick={() => setRole('ROLE_CUSTOMER')}
                  >
                    <User size={14} />
                    <span>Customer</span>
                  </button>
                  <button
                    type="button"
                    className={`btn ${role === 'ROLE_STAFF' ? 'btn-primary' : 'btn-secondary'}`}
                    style={{ fontSize: '0.85rem', padding: '8px' }}
                    onClick={() => setRole('ROLE_STAFF')}
                  >
                    <Building size={14} />
                    <span>Business Staff</span>
                  </button>
                </div>
              </div>
            )}

            {mode === 'register' && (
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  placeholder="e.g. Sarah Jenkins"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
              </div>
            )}

            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input
                type="email"
                className="form-input"
                required
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-input"
                required
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            {mode === 'register' && (
              <div className="form-group">
                <label className="form-label">Phone Number (Optional)</label>
                <input
                  type="tel"
                  className="form-input"
                  placeholder="(555) 123-4567"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </div>
            )}

            {mode === 'register' && role === 'ROLE_STAFF' && (
              <>
                <div style={{
                  padding: '12px',
                  background: 'var(--primary-light)',
                  borderRadius: '6px',
                  border: '1px solid var(--primary-border)',
                  marginBottom: '16px',
                  fontSize: '0.82rem',
                  color: 'var(--primary-hover)',
                }}>
                  Set up your business profile to start managing queues and client appointments.
                </div>

                <div className="form-group">
                  <label className="form-label">Business Name</label>
                  <input
                    type="text"
                    className="form-input"
                    required
                    placeholder="e.g. Apex Family Clinic or JumpZone Arena"
                    value={businessName}
                    onChange={(e) => setBusinessName(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select
                    className="form-select"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                  >
                    <option value="Clinics & Health">Clinics & Healthcare</option>
                    <option value="Play Areas & Kids">Play Areas & Family Entertainment</option>
                    <option value="Salons & Grooming">Salons & Personal Care</option>
                    <option value="Auto & Repairs">Auto & Equipment Repairs</option>
                    <option value="Pet Care">Pet Care & Grooming</option>
                    <option value="General Services">General Services</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">City / Neighborhood</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="e.g. Downtown"
                    value={city}
                    onChange={(e) => setCity(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Street Address</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="e.g. 100 Main St, Suite 4"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                </div>
              </>
            )}
          </div>

          <div className="modal-footer" style={{ justifyContent: 'space-between' }}>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={() => {
                setError('');
                setMode(mode === 'login' ? 'register' : 'login');
              }}
            >
              {mode === 'login' ? "Need an account? Register" : "Already registered? Sign in"}
            </button>

            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Please wait...' : mode === 'login' ? 'Sign In' : 'Create Account'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
