import React, { useState, useEffect } from 'react';
import {
  X, Clock, MapPin, Star, Users, Calendar,
  CheckCircle, AlertCircle, ChevronRight, MessageSquare, Send
} from 'lucide-react';
import { api } from '../api';

export default function BusinessModal({
  business,
  currentUser,
  onClose,
  onQueueJoined,
  onBookingCreated,
  onRequireAuth
}) {
  const [activeTab, setActiveTab] = useState('services'); // 'services', 'queue', 'booking', 'reviews'
  const [selectedServiceId, setSelectedServiceId] = useState(business?.services?.[0]?.id || null);
  const [notes, setNotes] = useState('');

  // Booking fields
  const [bookingDate, setBookingDate] = useState(() => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  });
  const [bookingTime, setBookingTime] = useState('10:00');

  // Reviews state
  const [reviews, setReviews] = useState([]);
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');

  // Feedback / Error
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (business?.id) {
      loadReviews();
    }
  }, [business?.id]);

  async function loadReviews() {
    try {
      const data = await api.getBusinessReviews(business.id);
      setReviews(data || []);
    } catch (e) {
      console.warn('Failed loading reviews:', e);
    }
  }

  const selectedService = business?.services?.find(s => s.id === Number(selectedServiceId)) || business?.services?.[0];
  const currentWait = (business?.waitingCount || 0) * (business?.avgServiceDurationMinutes || 20);

  async function handleJoinQueue(e) {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!currentUser) {
      onRequireAuth();
      return;
    }

    if (!selectedService) {
      setError('Please select a service');
      return;
    }

    setLoading(true);
    try {
      const res = await api.joinQueue({
        businessId: business.id,
        serviceId: selectedService.id,
        notes,
      });
      setSuccess(`Joined queue successfully! Your ticket number is ${res.ticketNumber}`);
      onQueueJoined(res);
      setTimeout(() => onClose(), 1500);
    } catch (err) {
      setError(err.message || 'Failed to join queue');
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateBooking(e) {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!currentUser) {
      onRequireAuth();
      return;
    }

    if (!selectedService) {
      setError('Please select a service');
      return;
    }

    setLoading(true);
    try {
      const res = await api.createBooking({
        businessId: business.id,
        serviceId: selectedService.id,
        bookingDate,
        startTime: bookingTime,
        notes,
      });
      setSuccess(`Appointment booked successfully for ${res.bookingDate} at ${res.startTime}`);
      onBookingCreated(res);
      setTimeout(() => onClose(), 1500);
    } catch (err) {
      setError(err.message || 'Failed to book appointment');
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmitReview(e) {
    e.preventDefault();
    setError('');

    if (!currentUser) {
      onRequireAuth();
      return;
    }

    setLoading(true);
    try {
      await api.addReview(business.id, {
        rating: reviewRating,
        comment: reviewComment,
      });
      setReviewComment('');
      setSuccess('Review submitted successfully!');
      loadReviews();
    } catch (err) {
      setError(err.message || 'Failed to submit review');
    } finally {
      setLoading(false);
    }
  }

  if (!business) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '680px' }}>
        {/* Header */}
        <div className="modal-header">
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
              <span className="business-category">{business.category}</span>
              <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#d97706', fontSize: '0.85rem', fontWeight: 600 }}>
                <Star size={14} fill="#d97706" />
                <span>{business.averageRating || '5.0'}</span>
                <span style={{ color: 'var(--text-light)', fontWeight: 400 }}>({business.reviewCount || 0} reviews)</span>
              </div>
            </div>
            <h2 style={{ fontSize: '1.4rem', color: 'var(--text-main)', margin: '4px 0' }}>
              {business.name}
            </h2>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px', color: 'var(--text-muted)', fontSize: '0.82rem' }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                <MapPin size={13} /> {business.address}{business.city ? `, ${business.city}` : ''}
              </span>
              <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                <Clock size={13} /> {business.openingHours}
              </span>
            </div>
          </div>
          <button className="modal-close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {/* Modal Navigation Tabs */}
        <div style={{ display: 'flex', borderBottom: '1px solid var(--border-color)', background: '#f8fafc', padding: '0 16px' }}>
          {[
            { id: 'services', label: 'Services & Prices', icon: ChevronRight },
            { id: 'queue', label: 'Join Walk-In Queue', icon: Users },
            { id: 'booking', label: 'Book Appointment', icon: Calendar },
            { id: 'reviews', label: 'Reviews', icon: MessageSquare },
          ].map(t => {
            const Icon = t.icon;
            const active = activeTab === t.id;
            return (
              <button
                key={t.id}
                onClick={() => { setActiveTab(t.id); setError(''); setSuccess(''); }}
                style={{
                  background: 'transparent',
                  border: 'none',
                  borderBottom: active ? '2px solid var(--primary)' : '2px solid transparent',
                  color: active ? 'var(--primary)' : 'var(--text-muted)',
                  padding: '12px 14px',
                  fontSize: '0.875rem',
                  fontWeight: active ? 600 : 500,
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  transition: 'all 0.15s'
                }}
              >
                <Icon size={14} />
                {t.label}
              </button>
            );
          })}
        </div>

        {/* Body */}
        <div className="modal-body">
          {error && (
            <div className="alert-box danger">
              <AlertCircle size={16} />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="alert-box success">
              <CheckCircle size={16} />
              <span>{success}</span>
            </div>
          )}

          {/* Tab 1: Services */}
          {activeTab === 'services' && (
            <div>
              <p style={{ color: 'var(--text-body)', fontSize: '0.9rem', marginBottom: '16px' }}>
                {business.description}
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {business.services?.map(service => (
                  <div
                    key={service.id}
                    style={{
                      background: '#ffffff',
                      border: '1px solid var(--border-color)',
                      borderRadius: '8px',
                      padding: '14px 16px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      gap: '16px',
                    }}
                  >
                    <div>
                      <div style={{ fontWeight: 600, color: 'var(--text-main)', fontSize: '0.95rem' }}>
                        {service.name}
                      </div>
                      <div style={{ color: 'var(--text-muted)', fontSize: '0.82rem', marginTop: '2px' }}>
                        {service.description}
                      </div>
                      <div style={{ display: 'flex', gap: '12px', marginTop: '6px', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                          <Clock size={12} style={{ color: 'var(--primary)' }} /> {service.durationMinutes} mins
                        </span>
                        <span style={{ fontWeight: 700, color: '#16a34a' }}>
                          ${Number(service.price).toFixed(2)}
                        </span>
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => {
                          setSelectedServiceId(service.id);
                          setActiveTab('queue');
                        }}
                      >
                        Join Queue
                      </button>
                      <button
                        className="btn btn-primary btn-sm"
                        onClick={() => {
                          setSelectedServiceId(service.id);
                          setActiveTab('booking');
                        }}
                      >
                        Book Slot
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tab 2: Join Virtual Queue */}
          {activeTab === 'queue' && (
            <form onSubmit={handleJoinQueue}>
              <div style={{
                background: '#f8fafc',
                border: '1px solid var(--border-color)',
                borderRadius: '8px',
                padding: '16px',
                marginBottom: '16px',
                display: 'flex',
                justifyContent: 'space-around',
                textAlign: 'center'
              }}>
                <div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>Waiting Ahead</div>
                  <div style={{ fontSize: '1.6rem', fontWeight: 700, color: 'var(--text-main)', marginTop: '2px' }}>
                    {business.waitingCount}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>people</div>
                </div>

                <div style={{ width: '1px', background: 'var(--border-color)' }} />

                <div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>Estimated Wait</div>
                  <div style={{ fontSize: '1.6rem', fontWeight: 700, color: '#b45309', marginTop: '2px' }}>
                    ~{currentWait}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: '#b45309' }}>minutes</div>
                </div>

                <div style={{ width: '1px', background: 'var(--border-color)' }} />

                <div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>Queue Status</div>
                  <div style={{ fontSize: '1.1rem', fontWeight: 700, color: business.queueOpen ? '#16a34a' : '#dc2626', marginTop: '6px' }}>
                    {business.queueOpen ? 'Open' : 'Paused'}
                  </div>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Select Service</label>
                <select
                  className="form-select"
                  value={selectedServiceId || ''}
                  onChange={(e) => setSelectedServiceId(Number(e.target.value))}
                  required
                >
                  {business.services?.map(s => (
                    <option key={s.id} value={s.id}>
                      {s.name} ({s.durationMinutes} mins) - ${Number(s.price).toFixed(2)}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Special Preferences or Notes (Optional)</label>
                <textarea
                  className="form-textarea"
                  rows={2}
                  placeholder="e.g. Any special preferences, stylist requests, or notes..."
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '20px' }}>
                <button
                  type="submit"
                  className="btn btn-primary"
                  style={{ width: '100%', padding: '10px' }}
                  disabled={loading || !business.queueOpen}
                >
                  {loading ? 'Joining Queue...' : 'Confirm & Join Walk-In Queue'}
                </button>
              </div>
            </form>
          )}

          {/* Tab 3: Book Appointment */}
          {activeTab === 'booking' && (
            <form onSubmit={handleCreateBooking}>
              <div className="form-group">
                <label className="form-label">Select Service</label>
                <select
                  className="form-select"
                  value={selectedServiceId || ''}
                  onChange={(e) => setSelectedServiceId(Number(e.target.value))}
                  required
                >
                  {business.services?.map(s => (
                    <option key={s.id} value={s.id}>
                      {s.name} ({s.durationMinutes} mins) - ${Number(s.price).toFixed(2)}
                    </option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Appointment Date</label>
                  <input
                    type="date"
                    className="form-input"
                    required
                    min={new Date().toISOString().split('T')[0]}
                    value={bookingDate}
                    onChange={(e) => setBookingDate(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Start Time ({business.openingHours})</label>
                  <input
                    type="time"
                    className="form-input"
                    required
                    value={bookingTime}
                    onChange={(e) => setBookingTime(e.target.value)}
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Booking Notes (Optional)</label>
                <textarea
                  className="form-textarea"
                  rows={2}
                  placeholder="e.g. First time visiting, or specific service requirements..."
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '20px' }}>
                <button
                  type="submit"
                  className="btn btn-primary"
                  style={{ width: '100%', padding: '10px' }}
                  disabled={loading}
                >
                  {loading ? 'Processing...' : 'Confirm Appointment Booking'}
                </button>
              </div>
            </form>
          )}

          {/* Tab 4: Reviews */}
          {activeTab === 'reviews' && (
            <div>
              {/* Write Review Form */}
              <form onSubmit={handleSubmitReview} style={{
                background: '#f8fafc',
                border: '1px solid var(--border-color)',
                borderRadius: '8px',
                padding: '16px',
                marginBottom: '20px',
              }}>
                <div style={{ fontWeight: 600, fontSize: '0.95rem', color: 'var(--text-main)', marginBottom: '10px' }}>
                  Write a Customer Review
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '12px' }}>
                  <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Rating:</span>
                  {[1, 2, 3, 4, 5].map(star => (
                    <button
                      key={star}
                      type="button"
                      style={{ background: 'transparent', border: 'none', cursor: 'pointer', padding: '2px' }}
                      onClick={() => setReviewRating(star)}
                    >
                      <Star
                        size={18}
                        color="#d97706"
                        fill={star <= reviewRating ? '#d97706' : 'none'}
                      />
                    </button>
                  ))}
                  <span style={{ fontSize: '0.85rem', color: '#d97706', fontWeight: 600, marginLeft: '6px' }}>
                    {reviewRating} of 5
                  </span>
                </div>

                <div className="form-group" style={{ marginBottom: '10px' }}>
                  <textarea
                    className="form-textarea"
                    rows={2}
                    placeholder="Share your experience (staff friendliness, wait time accuracy, haircut quality)..."
                    required
                    value={reviewComment}
                    onChange={(e) => setReviewComment(e.target.value)}
                  />
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                  <button type="submit" className="btn btn-primary btn-sm" disabled={loading}>
                    <Send size={13} />
                    <span>Submit Review</span>
                  </button>
                </div>
              </form>

              {/* Reviews List */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {reviews.length === 0 ? (
                  <div style={{ textAlign: 'center', padding: '20px', color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                    No reviews yet for this business.
                  </div>
                ) : (
                  reviews.map(r => (
                    <div
                      key={r.id}
                      style={{
                        background: '#ffffff',
                        border: '1px solid var(--border-color)',
                        borderRadius: '8px',
                        padding: '12px 16px',
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                        <span style={{ fontWeight: 600, color: 'var(--text-main)', fontSize: '0.9rem' }}>
                          {r.customerName}
                        </span>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
                          {[...Array(r.rating)].map((_, i) => (
                            <Star key={i} size={13} fill="#d97706" color="#d97706" />
                          ))}
                        </div>
                      </div>
                      <p style={{ color: 'var(--text-body)', fontSize: '0.85rem' }}>
                        {r.comment}
                      </p>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
