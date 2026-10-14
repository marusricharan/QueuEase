import React from 'react';
import { Clock, Users, Bell, X, CheckCircle2 } from 'lucide-react';

export default function ActiveTicketTracker({ activeTicket, onLeaveQueue }) {
  if (!activeTicket) return null;

  const isCalled = activeTicket.status === 'CALLED';
  const isInService = activeTicket.status === 'IN_SERVICE';

  return (
    <div
      className={`active-ticket-banner ${isCalled ? 'called-alert' : ''}`}
      style={{
        backgroundColor: isCalled ? '#f0fdf4' : isInService ? '#eff6ff' : '#ffffff',
        borderColor: isCalled ? '#16a34a' : isInService ? '#2563eb' : '#cbd5e1',
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '20px', flexWrap: 'wrap' }}>
        <div>
          <div style={{
            fontSize: '0.75rem',
            textTransform: 'uppercase',
            letterSpacing: '0.05em',
            color: isCalled ? '#15803d' : isInService ? '#1d4ed8' : '#2563eb',
            fontWeight: 700,
            marginBottom: '4px'
          }}>
            {isCalled ? '🔔 Your Turn! Please proceed to the chair' :
             isInService ? '✂️ Service In Progress' :
             '🎟️ Your Active Queue Ticket'}
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span className="ticket-badge">
              {activeTicket.ticketNumber}
            </span>
            <div>
              <div style={{ fontWeight: 700, fontSize: '1.1rem', color: 'var(--text-main)' }}>
                {activeTicket.businessName}
              </div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                {activeTicket.serviceName} ({activeTicket.serviceDuration} mins)
              </div>
            </div>
          </div>
        </div>

        {/* Dynamic Metrics */}
        {!isCalled && !isInService && (
          <div style={{ display: 'flex', gap: '20px', background: '#f8fafc', border: '1px solid #e2e8f0', padding: '8px 16px', borderRadius: '8px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Users size={18} style={{ color: 'var(--primary)' }} />
              <div>
                <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--text-main)', lineHeight: 1.1 }}>
                  {activeTicket.peopleAhead}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  {activeTicket.peopleAhead === 0 ? 'Next in line!' : `${activeTicket.peopleAhead} ahead`}
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Clock size={18} style={{ color: '#d97706' }} />
              <div>
                <div style={{ fontSize: '1.2rem', fontWeight: 700, color: '#b45309', lineHeight: 1.1 }}>
                  ~{activeTicket.estimatedWaitMinutes} mins
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Est. Wait Time</div>
              </div>
            </div>
          </div>
        )}

        {isCalled && (
          <div style={{
            background: '#dcfce7',
            padding: '8px 16px',
            borderRadius: '8px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            border: '1px solid #86efac',
          }}>
            <Bell size={22} style={{ color: '#15803d' }} />
            <div>
              <div style={{ fontWeight: 700, color: '#14532d', fontSize: '1.05rem' }}>
                Please proceed to Chair / Counter {activeTicket.counterNumber || 1}
              </div>
              <div style={{ fontSize: '0.78rem', color: '#166534' }}>
                Staff is ready to begin your service now!
              </div>
            </div>
          </div>
        )}

        {isInService && (
          <div style={{
            background: '#dbeafe',
            padding: '8px 16px',
            borderRadius: '8px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            border: '1px solid #93c5fd',
          }}>
            <CheckCircle2 size={20} style={{ color: '#1d4ed8' }} />
            <div>
              <div style={{ fontWeight: 700, color: '#1e3a8a', fontSize: '1rem' }}>
                Being Served at Chair {activeTicket.counterNumber || 1}
              </div>
              <div style={{ fontSize: '0.78rem', color: '#1e40af' }}>
                Enjoy your service!
              </div>
            </div>
          </div>
        )}
      </div>

      <div>
        <button
          className="btn btn-outline-danger btn-sm"
          onClick={() => {
            if (window.confirm("Are you sure you want to leave the live queue? Your ticket will be cancelled.")) {
              onLeaveQueue(activeTicket.id);
            }
          }}
        >
          <X size={14} />
          <span>Leave Queue</span>
        </button>
      </div>
    </div>
  );
}
