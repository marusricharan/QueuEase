import React, { useState, useEffect } from 'react';
import { ShieldCheck, Users, Building, CheckCircle } from 'lucide-react';
import { api } from '../api';

export default function AdminDashboardView() {
  const [activeTab, setActiveTab] = useState('users'); // 'users' or 'businesses'
  const [users, setUsers] = useState([]);
  const [businesses, setBusinesses] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    setLoading(true);
    try {
      const [u, b, s] = await Promise.all([
        api.getAdminUsers(),
        api.getAdminBusinesses(),
        api.getAdminStats(),
      ]);
      setUsers(u || []);
      setBusinesses(b || []);
      setStats(s);
    } catch (err) {
      console.warn('Admin load error:', err);
    } finally {
      setLoading(false);
    }
  }

  async function handleToggleUser(id) {
    try {
      await api.toggleUserActive(id);
      setMessage('User account status updated successfully');
      loadData();
      setTimeout(() => setMessage(''), 3000);
    } catch (e) {
      alert(e.message || 'Failed to toggle user status');
    }
  }

  async function handleToggleBusiness(id) {
    try {
      await api.toggleBusinessActive(id);
      setMessage('Business listing status updated successfully');
      loadData();
      setTimeout(() => setMessage(''), 3000);
    } catch (e) {
      alert(e.message || 'Failed to toggle business listing');
    }
  }

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--primary)', fontSize: '0.85rem', fontWeight: 600 }}>
          <ShieldCheck size={16} />
          <span>QueueEase Platform Administration</span>
        </div>
        <h2 style={{ fontSize: '1.75rem', color: 'var(--text-main)', marginTop: '4px' }}>
          System Console
        </h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Manage user accounts, approve or suspend business listings, and review platform metrics.
        </p>
      </div>

      {message && (
        <div className="alert-box success">
          <CheckCircle size={16} />
          <span>{message}</span>
        </div>
      )}

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--primary-light)', color: 'var(--primary)' }}>
            <Users size={22} />
          </div>
          <div>
            <div className="stat-val">{stats?.totalUsers || users.length}</div>
            <div className="stat-label">Total User Accounts</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fef3c7', color: '#b45309' }}>
            <Building size={22} />
          </div>
          <div>
            <div className="stat-val">{stats?.totalBusinesses || businesses.length}</div>
            <div className="stat-label">Registered Businesses</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#dcfce7', color: '#16a34a' }}>
            <CheckCircle size={22} />
          </div>
          <div>
            <div className="stat-val">{stats?.totalQueuesHandled || 5}</div>
            <div className="stat-label">Total Queue Tickets Processed</div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '8px', borderBottom: '1px solid var(--border-color)', marginBottom: '20px' }}>
        <button
          className={`pill-btn ${activeTab === 'users' ? 'active' : ''}`}
          onClick={() => setActiveTab('users')}
        >
          User Accounts ({users.length})
        </button>
        <button
          className={`pill-btn ${activeTab === 'businesses' ? 'active' : ''}`}
          onClick={() => setActiveTab('businesses')}
        >
          Business Listings ({businesses.length})
        </button>
      </div>

      {/* Table: Users */}
      {activeTab === 'users' && (
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>Email Address</th>
                <th>Role</th>
                <th>Phone</th>
                <th>Account Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id}>
                  <td>#{u.id}</td>
                  <td style={{ fontWeight: 600, color: 'var(--text-main)' }}>{u.name}</td>
                  <td>{u.email}</td>
                  <td>
                    <span className={`role-badge ${
                      u.role === 'ROLE_ADMIN' ? 'admin' :
                      u.role === 'ROLE_STAFF' ? 'staff' : 'customer'
                    }`}>
                      {u.role.replace('ROLE_', '')}
                    </span>
                  </td>
                  <td>{u.phone || '-'}</td>
                  <td>
                    <span className={`status-pill ${u.active ? 'completed' : 'cancelled'}`}>
                      {u.active ? 'Active' : 'Disabled'}
                    </span>
                  </td>
                  <td>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleToggleUser(u.id)}
                    >
                      {u.active ? 'Disable' : 'Activate'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Table: Businesses */}
      {activeTab === 'businesses' && (
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Business Name</th>
                <th>Category</th>
                <th>Address & Neighborhood</th>
                <th>Owner / Staff</th>
                <th>Daily Capacity</th>
                <th>Listing Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {businesses.map(b => (
                <tr key={b.id}>
                  <td>#{b.id}</td>
                  <td style={{ fontWeight: 600, color: 'var(--text-main)' }}>{b.name}</td>
                  <td>
                    <span className="business-category">{b.category}</span>
                  </td>
                  <td>{b.address}{b.city ? `, ${b.city}` : ''}</td>
                  <td>{b.ownerName || '-'}</td>
                  <td>{b.dailyCapacity} visits/day</td>
                  <td>
                    <span className={`status-pill ${b.active ? 'completed' : 'cancelled'}`}>
                      {b.active ? 'Active' : 'Suspended'}
                    </span>
                  </td>
                  <td>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleToggleBusiness(b.id)}
                    >
                      {b.active ? 'Suspend' : 'Approve'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
