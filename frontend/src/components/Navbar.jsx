import React from 'react';
import { Store, User, LogOut, LogIn, Calendar, Clock, ShieldCheck } from 'lucide-react';

export default function Navbar({
  activeTab,
  setActiveTab,
  currentUser,
  onLogout,
  onOpenAuth
}) {
  const isCustomer = !currentUser || currentUser.role === 'ROLE_CUSTOMER';
  const isStaff = currentUser && currentUser.role === 'ROLE_STAFF';
  const isAdmin = currentUser && currentUser.role === 'ROLE_ADMIN';

  return (
    <header className="navbar">
      {/* Brand */}
      <div
        className="brand-logo"
        style={{ cursor: 'pointer' }}
        onClick={() => setActiveTab('explore')}
      >
        <div className="brand-icon-box">
          <Store size={20} />
        </div>
        <div>
          <span className="brand-name">QueueEase</span>
          <span className="brand-badge">Local Businesses</span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="nav-links">
        <button
          className={`nav-item-btn ${activeTab === 'explore' ? 'active' : ''}`}
          onClick={() => setActiveTab('explore')}
        >
          <Store size={15} />
          <span>Browse Businesses</span>
        </button>

        {currentUser && isCustomer && (
          <>
            <button
              className={`nav-item-btn ${activeTab === 'my-bookings' ? 'active' : ''}`}
              onClick={() => setActiveTab('my-bookings')}
            >
              <Calendar size={15} />
              <span>My Appointments</span>
            </button>
            <button
              className={`nav-item-btn ${activeTab === 'my-history' ? 'active' : ''}`}
              onClick={() => setActiveTab('my-history')}
            >
              <Clock size={15} />
              <span>Queue History</span>
            </button>
          </>
        )}

        {(isStaff || isAdmin) && (
          <button
            className={`nav-item-btn ${activeTab === 'staff-dashboard' ? 'active' : ''}`}
            onClick={() => setActiveTab('staff-dashboard')}
          >
            <Clock size={15} />
            <span>Staff Dashboard</span>
          </button>
        )}

        {isAdmin && (
          <button
            className={`nav-item-btn ${activeTab === 'admin' ? 'active' : ''}`}
            onClick={() => setActiveTab('admin')}
          >
            <ShieldCheck size={15} />
            <span>Admin Console</span>
          </button>
        )}
      </nav>

      {/* Right User Actions */}
      <div className="nav-user-menu">
        {currentUser ? (
          <>
            <div className="user-pill">
              <User size={14} style={{ color: 'var(--text-muted)' }} />
              <span style={{ fontWeight: 600 }}>{currentUser.name}</span>
              <span className={`role-badge ${
                currentUser.role === 'ROLE_ADMIN' ? 'admin' :
                currentUser.role === 'ROLE_STAFF' ? 'staff' : 'customer'
              }`}>
                {currentUser.role === 'ROLE_ADMIN' ? 'Admin' :
                 currentUser.role === 'ROLE_STAFF' ? 'Staff' : 'Customer'}
              </span>
            </div>
            <button
              className="btn btn-secondary btn-sm"
              onClick={onLogout}
              title="Sign out of QueueEase"
            >
              <LogOut size={14} />
              <span>Sign Out</span>
            </button>
          </>
        ) : (
          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => onOpenAuth('login')}
            >
              <LogIn size={14} />
              <span>Sign In</span>
            </button>
            <button
              className="btn btn-primary btn-sm"
              onClick={() => onOpenAuth('register')}
            >
              <span>Register</span>
            </button>
          </div>
        )}
      </div>
    </header>
  );
}