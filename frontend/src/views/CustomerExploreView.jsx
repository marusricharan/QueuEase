import React from 'react';
import {
  Search, Star, Clock, MapPin, Users,
  Store, Calendar
} from 'lucide-react';
import ActiveTicketTracker from '../components/ActiveTicketTracker';

export default function CustomerExploreView({
  businesses,
  categories,
  selectedCategory,
  setSelectedCategory,
  searchQuery,
  setSearchQuery,
  onSelectBusiness,
  activeTicket,
  onLeaveQueue
}) {
  return (
    <div>
      {/* Active Ticket Banner if customer is currently in queue */}
      {activeTicket && (
        <ActiveTicketTracker
          activeTicket={activeTicket}
          onLeaveQueue={onLeaveQueue}
        />
      )}

      {/* Clean Hero Header */}
      <section className="hero-section">
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--primary)', marginBottom: '8px', fontSize: '0.85rem', fontWeight: 600 }}>
          <Store size={16} />
          <span>QueueEase Small Business Booking & Walk-In Queues</span>
        </div>
        <h1 className="hero-title">
          Book an Appointment or Join the Live Walk-In Queue
        </h1>
        <p className="hero-subtitle">
          Explore local service businesses—from healthcare clinics and kids indoor play parks to salons, automotive repairs, gadget fixes, and pet care. Check real-time wait times and join queues directly from your phone.
        </p>

        <div className="hero-features">
          <div className="hero-feature-item">
            <Clock size={16} style={{ color: 'var(--primary)' }} />
            <span>Dynamic Live Wait Estimates</span>
          </div>
          <div className="hero-feature-item">
            <Users size={16} style={{ color: '#16a34a' }} />
            <span>Real-time Queue Position Tracking</span>
          </div>
          <div className="hero-feature-item">
            <Calendar size={16} style={{ color: '#0284c7' }} />
            <span>Conflict-free Appointment Scheduling</span>
          </div>
        </div>
      </section>

      {/* Search & Category Filter Bar */}
      <div className="search-filter-bar">
        <div className="category-pills">
          {categories.map((cat) => (
            <button
              key={cat}
              className={`pill-btn ${selectedCategory === cat ? 'active' : ''}`}
              onClick={() => setSelectedCategory(cat)}
            >
              {cat}
            </button>
          ))}
        </div>

        <div className="search-input-wrapper">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search clinics, play areas, salons, auto repairs, pet care..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {/* Businesses Grid */}
      <div className="cards-grid">
        {businesses.length === 0 ? (
          <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '48px', color: 'var(--text-muted)', background: '#fff', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
            <Store size={32} style={{ color: 'var(--text-light)', marginBottom: '12px' }} />
            <p style={{ fontWeight: 600, color: 'var(--text-main)', marginBottom: '4px' }}>No businesses found</p>
            <p style={{ fontSize: '0.875rem' }}>Try clearing your search query or choosing "All".</p>
          </div>
        ) : (
          businesses.map((b) => {
            const hasWait = b.waitingCount > 0;
            return (
              <div
                key={b.id}
                className="business-card"
                onClick={() => onSelectBusiness(b)}
              >
                <div>
                  <div className="business-header">
                    <div>
                      <h3 className="business-title">{b.name}</h3>
                      <span className="business-category">{b.category}</span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#d97706', fontSize: '0.85rem', fontWeight: 600 }}>
                      <Star size={14} fill="#d97706" />
                      <span>{b.averageRating || '5.0'}</span>
                      <span style={{ color: 'var(--text-light)', fontWeight: 400 }}>({b.reviewCount || 0})</span>
                    </div>
                  </div>

                  <p className="business-desc">
                    {b.description}
                  </p>

                  {/* Live Queue Box */}
                  <div className="queue-preview-box">
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Clock size={16} style={{ color: hasWait ? 'var(--primary)' : '#16a34a' }} />
                      <div>
                        <div style={{ fontWeight: 600, fontSize: '0.875rem', color: 'var(--text-main)' }}>
                          {hasWait ? `~${b.currentWaitMinutes} mins estimated wait` : 'No wait right now'}
                        </div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                          {hasWait ? `${b.waitingCount} customer${b.waitingCount > 1 ? 's' : ''} in queue` : 'Immediate walk-in capacity available'}
                        </div>
                      </div>
                    </div>
                    <span className={`status-pill ${hasWait ? 'waiting' : 'completed'}`} style={{ fontSize: '0.7rem' }}>
                      {hasWait ? `${b.waitingCount} in line` : 'Open'}
                    </span>
                  </div>

                  <div className="business-meta">
                    <div className="meta-row">
                      <MapPin size={14} style={{ color: 'var(--text-muted)' }} />
                      <span>{b.address}{b.city ? `, ${b.city}` : ''}</span>
                    </div>
                    <div className="meta-row">
                      <Clock size={14} style={{ color: 'var(--text-muted)' }} />
                      <span>Hours: {b.openingHours || '08:30 AM - 07:30 PM'}</span>
                    </div>
                  </div>
                </div>

                <div className="business-actions">
                  <button
                    className="btn btn-secondary btn-sm"
                    onClick={(e) => { e.stopPropagation(); onSelectBusiness(b); }}
                  >
                    <Calendar size={14} />
                    <span>Book Slot</span>
                  </button>
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={(e) => { e.stopPropagation(); onSelectBusiness(b); }}
                  >
                    <Users size={14} />
                    <span>Join Queue</span>
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}
