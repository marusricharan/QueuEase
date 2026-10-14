import React from 'react';
import { Clock } from 'lucide-react';

export default function CustomerHistoryView({ history, loading }) {
  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '48px', color: 'var(--text-muted)' }}>
        Loading queue visit history...
      </div>
    );
  }

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.6rem', color: 'var(--text-main)', marginBottom: '4px' }}>
          Queue Visit History
        </h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Your history of walk-in queue requests, tickets called, and completed visits.
        </p>
      </div>

      {history.length === 0 ? (
        <div style={{
          background: '#ffffff',
          border: '1px solid var(--border-color)',
          borderRadius: '10px',
          padding: '48px',
          textAlign: 'center',
          color: 'var(--text-muted)'
        }}>
          <Clock size={40} style={{ color: 'var(--primary)', margin: '0 auto 12px', display: 'block', opacity: 0.8 }} />
          <h3 style={{ color: 'var(--text-main)', fontSize: '1.1rem', marginBottom: '6px' }}>No Queue History Yet</h3>
          <p style={{ fontSize: '0.875rem', maxWidth: '380px', margin: '0 auto' }}>
            When you join live walk-in queues at local businesses, your visit timeline and ticket statuses will appear here.
          </p>
        </div>
      ) : (
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>Ticket</th>
                <th>Business & Service</th>
                <th>Joined Time</th>
                <th>Status</th>
                <th>Station / Counter</th>
                <th>Est. Wait</th>
              </tr>
            </thead>
            <tbody>
              {history.map((h) => {
                const joined = h.joinedTime ? new Date(h.joinedTime).toLocaleString([], {
                  month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
                }) : '-';

                return (
                  <tr key={h.id}>
                    <td>
                      <span className="status-pill waiting" style={{ fontWeight: 700, fontSize: '0.85rem' }}>
                        {h.ticketNumber}
                      </span>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--text-main)' }}>{h.businessName}</div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{h.serviceName}</div>
                    </td>
                    <td style={{ fontSize: '0.85rem', color: 'var(--text-body)' }}>
                      {joined}
                    </td>
                    <td>
                      <span className={`status-pill ${
                        h.status === 'WAITING' ? 'waiting' :
                        h.status === 'CALLED' ? 'called' :
                        h.status === 'IN_SERVICE' ? 'in-service' :
                        h.status === 'COMPLETED' ? 'completed' : 'cancelled'
                      }`}>
                        {h.status}
                      </span>
                    </td>
                    <td style={{ color: 'var(--text-body)' }}>
                      {h.counterNumber ? `Chair ${h.counterNumber}` : '-'}
                    </td>
                    <td style={{ color: '#b45309', fontWeight: 600 }}>
                      {h.estimatedWaitMinutes ? `~${h.estimatedWaitMinutes} mins` : '-'}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
