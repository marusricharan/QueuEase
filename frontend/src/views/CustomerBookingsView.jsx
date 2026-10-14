import React from 'react';
import { Calendar, Clock, MapPin, X } from 'lucide-react';

export default function CustomerBookingsView({ bookings, onCancelBooking, loading }) {
  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '48px', color: 'var(--text-muted)' }}>
        Loading your appointments...
      </div>
    );
  }

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.6rem', color: 'var(--text-main)', marginBottom: '4px' }}>
          My Booked Appointments
        </h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          View, manage, and track your scheduled appointments and services.
        </p>
      </div>

      {bookings.length === 0 ? (
        <div style={{
          background: '#ffffff',
          border: '1px solid var(--border-color)',
          borderRadius: '10px',
          padding: '48px',
          textAlign: 'center',
          color: 'var(--text-muted)'
        }}>
          <Calendar size={40} style={{ color: 'var(--primary)', margin: '0 auto 12px', display: 'block', opacity: 0.8 }} />
          <h3 style={{ color: 'var(--text-main)', fontSize: '1.1rem', marginBottom: '6px' }}>No Appointments Booked</h3>
          <p style={{ fontSize: '0.875rem', maxWidth: '380px', margin: '0 auto' }}>
            You don't have any scheduled appointments yet. Browse local clinics, play parks, salons, auto shops, or pet spas to reserve your slot.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {bookings.map((b) => {
            const isConfirmed = b.status === 'CONFIRMED';
            return (
              <div
                key={b.id}
                style={{
                  background: '#ffffff',
                  border: '1px solid var(--border-color)',
                  borderRadius: '10px',
                  padding: '18px 22px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  gap: '20px',
                  flexWrap: 'wrap',
                  boxShadow: 'var(--shadow-sm)'
                }}
              >
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                    <span className={`status-pill ${
                      b.status === 'CONFIRMED' ? 'called' :
                      b.status === 'IN_SERVICE' ? 'in-service' :
                      b.status === 'COMPLETED' ? 'completed' : 'cancelled'
                    }`}>
                      {b.status}
                    </span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--primary)', fontWeight: 600 }}>
                      {b.businessCategory}
                    </span>
                  </div>

                  <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', marginBottom: '2px' }}>
                    {b.serviceName}
                  </h3>
                  <div style={{ fontWeight: 600, color: 'var(--text-body)', fontSize: '0.92rem' }}>
                    {b.businessName}
                  </div>

                  <div style={{ display: 'flex', gap: '16px', fontSize: '0.82rem', color: 'var(--text-muted)', marginTop: '8px', flexWrap: 'wrap' }}>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                      <Calendar size={13} style={{ color: 'var(--primary)' }} /> {b.bookingDate}
                    </span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                      <Clock size={13} style={{ color: '#d97706' }} /> {b.startTime} - {b.endTime} ({b.serviceDuration} mins)
                    </span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                      <MapPin size={13} /> {b.businessAddress}
                    </span>
                  </div>

                  {b.notes && (
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontStyle: 'italic', marginTop: '6px' }}>
                      Notes: "{b.notes}"
                    </div>
                  )}
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '1.25rem', fontWeight: 700, color: '#16a34a' }}>
                      ${Number(b.servicePrice).toFixed(2)}
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Service price</div>
                  </div>

                  {isConfirmed && (
                    <button
                      className="btn btn-outline-danger btn-sm"
                      onClick={() => {
                        if (window.confirm('Cancel this appointment booking?')) {
                          onCancelBooking(b.id);
                        }
                      }}
                    >
                      <X size={14} />
                      <span>Cancel</span>
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
