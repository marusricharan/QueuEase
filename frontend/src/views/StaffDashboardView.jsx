import React, { useState, useEffect } from 'react';
import {
  Users, UserPlus, PhoneCall, CheckCircle, XCircle, Clock,
  Calendar, BarChart2, Settings, Plus, Play, Check, RotateCcw, Building
} from 'lucide-react';
import { api } from '../api';

export default function StaffDashboardView({
  businessId,
  businesses,
  onChangeBusiness,
  currentUser
}) {
  const [subTab, setSubTab] = useState('live-queue'); // 'live-queue', 'appointments', 'analytics', 'settings'
  const [queue, setQueue] = useState([]);
  const [completedToday, setCompletedToday] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [business, setBusiness] = useState(null);

  const [counterNumber, setCounterNumber] = useState(1);
  const [showWalkInModal, setShowWalkInModal] = useState(false);
  const [showAddServiceModal, setShowAddServiceModal] = useState(false);

  // Walk-in form
  const [walkInName, setWalkInName] = useState('');
  const [walkInPhone, setWalkInPhone] = useState('');
  const [walkInServiceId, setWalkInServiceId] = useState('');

  // Service form
  const [newServiceName, setNewServiceName] = useState('');
  const [newServiceDesc, setNewServiceDesc] = useState('');
  const [newServiceDuration, setNewServiceDuration] = useState(30);
  const [newServicePrice, setNewServicePrice] = useState('40.00');

  // Business settings
  const [avgDuration, setAvgDuration] = useState(20);
  const [dailyCapacity, setDailyCapacity] = useState(50);
  const [isQueueOpen, setIsQueueOpen] = useState(true);

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  // Selected active business
  const currentBusinessId = businessId || (businesses?.[0]?.id);

  useEffect(() => {
    if (currentBusinessId) {
      loadAllData();
      const interval = setInterval(loadQueueData, 3500); // Polling every 3.5s
      return () => clearInterval(interval);
    }
  }, [currentBusinessId]);

  async function loadAllData() {
    setLoading(true);
    await Promise.all([
      loadQueueData(),
      loadAppointmentsData(),
      loadAnalyticsData(),
      loadBusinessDetails(),
    ]);
    setLoading(false);
  }

  async function loadQueueData() {
    try {
      const [liveQ, comp] = await Promise.all([
        api.getStaffQueue(currentBusinessId),
        api.getStaffCompletedToday(currentBusinessId)
      ]);
      setQueue(liveQ || []);
      setCompletedToday(comp || []);
    } catch (e) {
      console.warn('Queue load error:', e);
    }
  }

  async function loadAppointmentsData() {
    try {
      const data = await api.getBusinessBookings(currentBusinessId);
      setAppointments(data || []);
    } catch (e) {
      console.warn('Appointments load error:', e);
    }
  }

  async function loadAnalyticsData() {
    try {
      const data = await api.getStaffAnalytics(currentBusinessId);
      setAnalytics(data);
    } catch (e) {
      console.warn('Analytics load error:', e);
    }
  }

  async function loadBusinessDetails() {
    try {
      const data = await api.getBusinessById(currentBusinessId);
      setBusiness(data);
      if (data) {
        setAvgDuration(data.avgServiceDurationMinutes || 20);
        setDailyCapacity(data.dailyCapacity || 50);
        setIsQueueOpen(data.queueOpen !== false);
        if (data.services?.length > 0 && !walkInServiceId) {
          setWalkInServiceId(data.services[0].id);
        }
      }
    } catch (e) {
      console.warn('Business details load error:', e);
    }
  }

  async function handleCallNext() {
    try {
      const res = await api.callNext(currentBusinessId, counterNumber);
      setMessage(`Called ticket ${res.ticketNumber} to Station / Counter ${res.counterNumber}`);
      loadQueueData();
      loadAnalyticsData();
      setTimeout(() => setMessage(''), 4000);
    } catch (err) {
      alert(err.message || 'Failed to call next customer');
    }
  }

  async function handleUpdateStatus(id, newStatus) {
    try {
      await api.updateQueueStatus(id, { status: newStatus, counterNumber });
      loadQueueData();
      loadAnalyticsData();
    } catch (err) {
      alert(err.message || 'Failed to update status');
    }
  }

  async function handleAddWalkIn(e) {
    e.preventDefault();
    if (!walkInName.trim()) return;

    try {
      const res = await api.addWalkIn(currentBusinessId, {
        customerName: walkInName,
        customerPhone: walkInPhone,
        serviceId: Number(walkInServiceId),
      });
      setShowWalkInModal(false);
      setWalkInName('');
      setWalkInPhone('');
      setMessage(`Walk-in customer added with ticket ${res.ticketNumber}`);
      loadQueueData();
      loadAnalyticsData();
      setTimeout(() => setMessage(''), 4000);
    } catch (err) {
      alert(err.message || 'Failed to add walk-in');
    }
  }

  async function handleSaveSettings(e) {
    e.preventDefault();
    try {
      await api.updateBusiness(currentBusinessId, {
        ...business,
        avgServiceDurationMinutes: Number(avgDuration),
        dailyCapacity: Number(dailyCapacity),
        queueOpen: isQueueOpen,
      });
      setMessage('Business operational settings updated successfully.');
      loadBusinessDetails();
      loadQueueData();
      setTimeout(() => setMessage(''), 4000);
    } catch (err) {
      alert(err.message || 'Failed to update settings');
    }
  }

  async function handleAddService(e) {
    e.preventDefault();
    try {
      await api.addService(currentBusinessId, {
        name: newServiceName,
        description: newServiceDesc,
        durationMinutes: Number(newServiceDuration),
        price: Number(newServicePrice),
      });
      setShowAddServiceModal(false);
      setNewServiceName('');
      setNewServiceDesc('');
      loadBusinessDetails();
      setMessage('New service added successfully.');
      setTimeout(() => setMessage(''), 4000);
    } catch (err) {
      alert(err.message || 'Failed to add service');
    }
  }

  // Derive active items
  const currentlyServing = queue.filter(q => q.status === 'IN_SERVICE' || q.status === 'CALLED');
  const waitingCustomers = queue.filter(q => q.status === 'WAITING');

  return (
    <div>
      {/* Header with Business Selector & Title */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--primary)', fontSize: '0.85rem', fontWeight: 600 }}>
            <Building size={16} />
            <span>Staff Dashboard</span>
          </div>
          <h2 style={{ fontSize: '1.75rem', color: 'var(--text-main)', marginTop: '4px' }}>
            {business?.name || 'Live Operations'}
          </h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Manage appointments, call walk-in customers, and update service progress in real-time.
          </p>
        </div>

        {businesses && businesses.length > 1 && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>Business:</span>
            <select
              className="form-select"
              style={{ width: 'auto' }}
              value={currentBusinessId}
              onChange={(e) => onChangeBusiness(Number(e.target.value))}
            >
              {businesses.map(b => (
                <option key={b.id} value={b.id}>{b.name}</option>
              ))}
            </select>
          </div>
        )}
      </div>

      {message && (
        <div className="alert-box success">
          <CheckCircle size={16} />
          <span>{message}</span>
        </div>
      )}

      {/* Top Stat Ribbons */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fef3c7', color: '#b45309' }}>
            <Users size={22} />
          </div>
          <div>
            <div className="stat-val">{waitingCustomers.length}</div>
            <div className="stat-label">Waiting in Queue</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#dbeafe', color: 'var(--primary)' }}>
            <Play size={22} />
          </div>
          <div>
            <div className="stat-val">{currentlyServing.length}</div>
            <div className="stat-label">In Service / Called</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fef9c3', color: '#854d0e' }}>
            <Clock size={22} />
          </div>
          <div>
            <div className="stat-val">
              ~{waitingCustomers.length * (business?.avgServiceDurationMinutes || 20)}m
            </div>
            <div className="stat-label">Est. Queue Wait</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#dcfce7', color: '#16a34a' }}>
            <CheckCircle size={22} />
          </div>
          <div>
            <div className="stat-val">{analytics?.totalServed ?? completedToday.length}</div>
            <div className="stat-label">Served Today</div>
          </div>
        </div>
      </div>

      {/* Navigation Sub-Tabs */}
      <div className="subtabs-nav">
        {[
          { id: 'live-queue', label: 'Active Queue', icon: Users, count: queue.length },
          { id: 'appointments', label: 'Today\'s Appointments', icon: Calendar, count: appointments.length },
          { id: 'analytics', label: 'Daily Summary', icon: BarChart2 },
          { id: 'settings', label: 'Services & Rules', icon: Settings },
        ].map(t => {
          const Icon = t.icon;
          const active = subTab === t.id;
          return (
            <button
              key={t.id}
              className={`subtab-btn ${active ? 'active' : ''}`}
              onClick={() => setSubTab(t.id)}
            >
              <Icon size={15} />
              <span>{t.label}</span>
              {t.count !== undefined && (
                <span className="badge-stamp" style={{
                  fontSize: '0.75rem',
                  padding: '1px 6px',
                  background: active ? 'var(--primary-light)' : 'var(--bg-subtle)',
                  color: active ? 'var(--primary)' : 'var(--text-muted)',
                  borderRadius: '10px'
                }}>
                  {t.count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* TAB 1: Live Queue */}
      {subTab === 'live-queue' && (
        <div>
          {/* Quick Command Action Ribbon */}
          <div style={{
            background: '#ffffff',
            border: '1px solid var(--border-color)',
            borderRadius: '10px',
            padding: '18px 20px',
            marginBottom: '20px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '14px',
            boxShadow: 'var(--shadow-sm)'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '14px', flexWrap: 'wrap' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '0.875rem', color: 'var(--text-body)', fontWeight: 500 }}>Station / Counter:</span>
                <select
                  className="form-select"
                  style={{ width: 'auto', padding: '6px 12px' }}
                  value={counterNumber}
                  onChange={(e) => setCounterNumber(Number(e.target.value))}
                >
                  <option value={1}>Station 1 (Room / Counter / Chair 1)</option>
                  <option value={2}>Station 2 (Room / Counter / Chair 2)</option>
                  <option value={3}>Station 3 (Room / Counter / Chair 3)</option>
                </select>
              </div>

              <button
                className="btn btn-primary"
                onClick={handleCallNext}
                disabled={waitingCustomers.length === 0}
              >
                <PhoneCall size={16} />
                <span>Call Next Customer ({waitingCustomers.length} waiting)</span>
              </button>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                className="btn btn-secondary"
                onClick={() => setShowWalkInModal(true)}
              >
                <UserPlus size={15} />
                <span>+ Add Walk-In Customer</span>
              </button>
            </div>
          </div>

          {/* Currently Serving & Called Highlight Banner */}
          {currentlyServing.length > 0 && (
            <div style={{
              background: '#f0fdf4',
              border: '1px solid #bbf7d0',
              borderRadius: '10px',
              padding: '18px 20px',
              marginBottom: '24px',
            }}>
              <div style={{ fontSize: '0.8rem', fontWeight: 700, color: '#166534', textTransform: 'uppercase', marginBottom: '12px' }}>
                Active Station / Customer In Service
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '16px' }}>
                {currentlyServing.map(item => (
                  <div
                    key={item.id}
                    style={{
                      background: '#ffffff',
                      border: '1px solid #bbf7d0',
                      borderRadius: '8px',
                      padding: '16px',
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'space-between',
                      gap: '12px',
                    }}
                  >
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <span className="ticket-badge" style={{ fontSize: '1.1rem', padding: '4px 10px' }}>{item.ticketNumber}</span>
                        <span className={`status-pill ${item.status === 'CALLED' ? 'called' : 'in-service'}`}>
                          {item.status}
                        </span>
                      </div>
                      <div style={{ fontWeight: 700, fontSize: '1.05rem', color: 'var(--text-main)', marginTop: '8px' }}>
                        {item.customerName}
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                        {item.serviceName} ({item.serviceDuration} mins)
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--primary)', fontWeight: 600, marginTop: '4px' }}>
                        Station / Counter {item.counterNumber || 1}
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '8px', marginTop: '6px' }}>
                      {item.status === 'CALLED' ? (
                        <>
                          <button
                            className="btn btn-success btn-sm"
                            style={{ flex: 1 }}
                            onClick={() => handleUpdateStatus(item.id, 'IN_SERVICE')}
                          >
                            <Play size={13} />
                            <span>Start Service</span>
                          </button>
                          <button
                            className="btn btn-secondary btn-sm"
                            title="Re-call customer"
                            onClick={() => handleUpdateStatus(item.id, 'CALLED')}
                          >
                            <RotateCcw size={13} />
                          </button>
                          <button
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => handleUpdateStatus(item.id, 'NO_SHOW')}
                          >
                            No-Show
                          </button>
                        </>
                      ) : (
                        <>
                          <button
                            className="btn btn-success btn-sm"
                            style={{ flex: 1 }}
                            onClick={() => handleUpdateStatus(item.id, 'COMPLETED')}
                          >
                            <Check size={13} />
                            <span>Complete Service</span>
                          </button>
                          <button
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => handleUpdateStatus(item.id, 'CANCELLED')}
                          >
                            Cancel
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Live Waiting Queue Table */}
          <div style={{ marginBottom: '24px' }}>
            <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', marginBottom: '12px' }}>
              Waiting List ({waitingCustomers.length} customers)
            </h3>

            {waitingCustomers.length === 0 ? (
              <div style={{
                background: '#ffffff',
                border: '1px solid var(--border-color)',
                borderRadius: '10px',
                padding: '36px',
                textAlign: 'center',
                color: 'var(--text-muted)'
              }}>
                <Users size={32} style={{ color: 'var(--primary)', margin: '0 auto 10px', opacity: 0.6 }} />
                <div style={{ fontWeight: 600, color: 'var(--text-main)' }}>The waiting queue is currently empty</div>
                <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                  Walk-ins or customers joining from the website will appear here in real time.
                </div>
              </div>
            ) : (
              <div className="table-container">
                <table className="custom-table">
                  <thead>
                    <tr>
                      <th>Queue Position</th>
                      <th>Ticket #</th>
                      <th>Customer Name</th>
                      <th>Service</th>
                      <th>Joined Time</th>
                      <th>Estimated Wait</th>
                      <th>Staff Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {waitingCustomers.map((w, index) => {
                      const joinedTime = w.joinedTime ? new Date(w.joinedTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '-';
                      return (
                        <tr key={w.id}>
                          <td style={{ fontWeight: 700, color: 'var(--primary)' }}>
                            #{index + 1}
                          </td>
                          <td>
                            <span className="status-pill waiting" style={{ fontWeight: 700, fontSize: '0.85rem' }}>
                              {w.ticketNumber}
                            </span>
                          </td>
                          <td>
                            <div style={{ fontWeight: 600, color: 'var(--text-main)' }}>{w.customerName}</div>
                            {w.customerPhone && (
                              <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>{w.customerPhone}</div>
                            )}
                          </td>
                          <td>
                            <div style={{ color: 'var(--text-main)' }}>{w.serviceName}</div>
                            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>{w.serviceDuration} mins</div>
                          </td>
                          <td style={{ color: 'var(--text-body)', fontSize: '0.85rem' }}>{joinedTime}</td>
                          <td style={{ fontWeight: 700, color: '#b45309' }}>
                            ~{w.estimatedWaitMinutes} mins
                          </td>
                          <td>
                            <div style={{ display: 'flex', gap: '6px' }}>
                              <button
                                className="btn btn-primary btn-sm"
                                onClick={() => handleUpdateStatus(w.id, 'CALLED')}
                              >
                                Call Now
                              </button>
                              <button
                                className="btn btn-outline-danger btn-sm"
                                onClick={() => handleUpdateStatus(w.id, 'CANCELLED')}
                              >
                                Cancel
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 2: Appointments */}
      {subTab === 'appointments' && (
        <div>
          <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', marginBottom: '12px' }}>
            Scheduled Appointments ({appointments.length})
          </h3>

          {appointments.length === 0 ? (
            <div style={{ background: '#ffffff', border: '1px solid var(--border-color)', borderRadius: '10px', padding: '36px', textAlign: 'center', color: 'var(--text-muted)' }}>
              No appointments scheduled for this business yet.
            </div>
          ) : (
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Date & Time</th>
                    <th>Customer Name</th>
                    <th>Service</th>
                    <th>Price</th>
                    <th>Status</th>
                    <th>Notes</th>
                    <th>Update Status</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map((appt) => (
                    <tr key={appt.id}>
                      <td style={{ fontWeight: 600, color: 'var(--text-main)' }}>
                        {appt.bookingDate} · {appt.startTime} - {appt.endTime}
                      </td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{appt.customerName}</div>
                      </td>
                      <td>{appt.serviceName} ({appt.serviceDuration}m)</td>
                      <td style={{ color: '#16a34a', fontWeight: 600 }}>${Number(appt.servicePrice).toFixed(2)}</td>
                      <td>
                        <span className={`status-pill ${
                          appt.status === 'CONFIRMED' ? 'called' :
                          appt.status === 'IN_SERVICE' ? 'in-service' :
                          appt.status === 'COMPLETED' ? 'completed' : 'cancelled'
                        }`}>
                          {appt.status}
                        </span>
                      </td>
                      <td style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                        {appt.notes || '-'}
                      </td>
                      <td>
                        {appt.status === 'CONFIRMED' && (
                          <div style={{ display: 'flex', gap: '6px' }}>
                            <button
                              className="btn btn-primary btn-sm"
                              onClick={async () => {
                                await api.updateBookingStatus(appt.id, { status: 'IN_SERVICE' });
                                loadAppointmentsData();
                              }}
                            >
                              In Service
                            </button>
                            <button
                              className="btn btn-success btn-sm"
                              onClick={async () => {
                                await api.updateBookingStatus(appt.id, { status: 'COMPLETED' });
                                loadAppointmentsData();
                              }}
                            >
                              Complete
                            </button>
                            <button
                              className="btn btn-outline-danger btn-sm"
                              onClick={async () => {
                                await api.updateBookingStatus(appt.id, { status: 'CANCELLED' });
                                loadAppointmentsData();
                              }}
                            >
                              Cancel
                            </button>
                          </div>
                        )}
                        {appt.status === 'IN_SERVICE' && (
                          <button
                            className="btn btn-success btn-sm"
                            onClick={async () => {
                              await api.updateBookingStatus(appt.id, { status: 'COMPLETED' });
                              loadAppointmentsData();
                            }}
                          >
                            Mark Completed
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* TAB 3: Analytics */}
      {subTab === 'analytics' && analytics && (
        <div>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon" style={{ background: '#dcfce7', color: '#16a34a' }}>
                <CheckCircle size={22} />
              </div>
              <div>
                <div className="stat-val">{analytics.totalServed}</div>
                <div className="stat-label">Total Visits Served</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: '#fee2e2', color: '#dc2626' }}>
                <XCircle size={22} />
              </div>
              <div>
                <div className="stat-val">{analytics.totalCancelled + analytics.totalNoShow}</div>
                <div className="stat-label">Cancellations & No-Shows</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: '#fef3c7', color: '#b45309' }}>
                <Clock size={22} />
              </div>
              <div>
                <div className="stat-val">{analytics.avgWaitTimeMinutes}m</div>
                <div className="stat-label">Average Wait Time</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon" style={{ background: 'var(--primary-light)', color: 'var(--primary)' }}>
                <Calendar size={22} />
              </div>
              <div>
                <div className="stat-val">{analytics.totalAppointments}</div>
                <div className="stat-label">Confirmed Bookings</div>
              </div>
            </div>
          </div>

          {/* Peak Hours Hourly Chart */}
          <div style={{
            background: '#ffffff',
            border: '1px solid var(--border-color)',
            borderRadius: '10px',
            padding: '24px',
            boxShadow: 'var(--shadow-sm)'
          }}>
            <div style={{ marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', margin: 0 }}>
                Hourly Customer Flow
              </h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '4px' }}>
                Arrival traffic across business hours today
              </p>
            </div>

            <div style={{
              display: 'flex',
              alignItems: 'flex-end',
              gap: '12px',
              height: '180px',
              paddingTop: '20px',
              borderBottom: '1px solid var(--border-color)',
            }}>
              {analytics.hourlyTraffic?.map((h) => {
                const maxVal = Math.max(...(analytics.hourlyTraffic.map(x => x.count)), 5);
                const heightPercent = Math.max((h.count / maxVal) * 100, 8);

                return (
                  <div key={h.hourLabel} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', height: '100%', justifyContent: 'flex-end' }}>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-main)', marginBottom: '4px' }}>
                      {h.count > 0 ? h.count : ''}
                    </div>
                    <div
                      style={{
                        width: '100%',
                        maxWidth: '36px',
                        height: `${heightPercent}%`,
                        backgroundColor: h.count > 0 ? 'var(--primary)' : '#e2e8f0',
                        borderRadius: '4px 4px 0 0',
                        transition: 'height 0.3s'
                      }}
                      title={`${h.hourLabel}: ${h.count} customers`}
                    />
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '8px' }}>
                      {h.hourLabel}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* TAB 4: Settings & Services */}
      {subTab === 'settings' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '20px' }}>
          {/* Operational Settings Form */}
          <div style={{ background: '#ffffff', border: '1px solid var(--border-color)', borderRadius: '10px', padding: '24px', boxShadow: 'var(--shadow-sm)' }}>
            <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', marginBottom: '16px' }}>
              Queue & Capacity Rules
            </h3>
            <form onSubmit={handleSaveSettings}>
              <div className="form-group">
                <label className="form-label">Average Service Duration (Minutes)</label>
                <input
                  type="number"
                  className="form-input"
                  min="5"
                  max="180"
                  required
                  value={avgDuration}
                  onChange={(e) => setAvgDuration(e.target.value)}
                />
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '4px', display: 'block' }}>
                  Queue wait calculation uses this to forecast wait times for waiting customers.
                </span>
              </div>

              <div className="form-group">
                <label className="form-label">Daily Walk-In Queue Capacity</label>
                <input
                  type="number"
                  className="form-input"
                  min="1"
                  max="200"
                  required
                  value={dailyCapacity}
                  onChange={(e) => setDailyCapacity(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Queue Availability Status</label>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button
                    type="button"
                    className={`btn ${isQueueOpen ? 'btn-success' : 'btn-secondary'}`}
                    onClick={() => setIsQueueOpen(true)}
                    style={{ flex: 1 }}
                  >
                    Open & Accepting
                  </button>
                  <button
                    type="button"
                    className={`btn ${!isQueueOpen ? 'btn-danger' : 'btn-secondary'}`}
                    onClick={() => setIsQueueOpen(false)}
                    style={{ flex: 1 }}
                  >
                    Pause Queue
                  </button>
                </div>
              </div>

              <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '8px' }}>
                Save Settings
              </button>
            </form>
          </div>

          {/* Services List and Add Service */}
          <div style={{ background: '#ffffff', border: '1px solid var(--border-color)', borderRadius: '10px', padding: '24px', boxShadow: 'var(--shadow-sm)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', margin: 0 }}>
                Services Offered ({business?.services?.length || 0})
              </h3>
              <button
                className="btn btn-primary btn-sm"
                onClick={() => setShowAddServiceModal(true)}
              >
                <Plus size={14} />
                <span>Add Service</span>
              </button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {business?.services?.map((svc) => (
                <div
                  key={svc.id}
                  style={{
                    background: '#f8fafc',
                    border: '1px solid var(--border-color)',
                    borderRadius: '8px',
                    padding: '12px 14px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, color: 'var(--text-main)', fontSize: '0.92rem' }}>{svc.name}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      {svc.durationMinutes} mins · ${Number(svc.price).toFixed(2)}
                    </div>
                  </div>
                  <button
                    className="btn btn-outline-danger btn-sm"
                    onClick={async () => {
                      if (window.confirm(`Delete service "${svc.name}"?`)) {
                        await api.deleteService(svc.id);
                        loadBusinessDetails();
                      }
                    }}
                  >
                    Remove
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Modal: Add Walk-In Customer */}
      {showWalkInModal && (
        <div className="modal-overlay" onClick={() => setShowWalkInModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '440px' }}>
            <div className="modal-header">
              <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', margin: 0 }}>
                Add Walk-In Customer
              </h3>
              <button className="modal-close-btn" onClick={() => setShowWalkInModal(false)}>
                ✕
              </button>
            </div>
            <form onSubmit={handleAddWalkIn}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Customer Name</label>
                  <input
                    type="text"
                    className="form-input"
                    required
                    placeholder="e.g. Liam Smith"
                    value={walkInName}
                    onChange={(e) => setWalkInName(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Phone Number (Optional)</label>
                  <input
                    type="tel"
                    className="form-input"
                    placeholder="e.g. (555) 012-3456"
                    value={walkInPhone}
                    onChange={(e) => setWalkInPhone(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Select Service</label>
                  <select
                    className="form-select"
                    value={walkInServiceId}
                    onChange={(e) => setWalkInServiceId(e.target.value)}
                    required
                  >
                    {business?.services?.map(s => (
                      <option key={s.id} value={s.id}>
                        {s.name} ({s.durationMinutes}m) - ${Number(s.price).toFixed(2)}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowWalkInModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Issue Ticket & Enqueue
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Add New Service */}
      {showAddServiceModal && (
        <div className="modal-overlay" onClick={() => setShowAddServiceModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '440px' }}>
            <div className="modal-header">
              <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', margin: 0 }}>
                Add New Service
              </h3>
              <button className="modal-close-btn" onClick={() => setShowAddServiceModal(false)}>
                ✕
              </button>
            </div>
            <form onSubmit={handleAddService}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Service Name</label>
                  <input
                    type="text"
                    className="form-input"
                    required
                    placeholder="e.g. General Consultation or Express Service"
                    value={newServiceName}
                    onChange={(e) => setNewServiceName(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Description</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="Brief description of service"
                    value={newServiceDesc}
                    onChange={(e) => setNewServiceDesc(e.target.value)}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                  <div className="form-group">
                    <label className="form-label">Duration (Minutes)</label>
                    <input
                      type="number"
                      className="form-input"
                      required
                      min="5"
                      value={newServiceDuration}
                      onChange={(e) => setNewServiceDuration(e.target.value)}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Price ($)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      required
                      value={newServicePrice}
                      onChange={(e) => setNewServicePrice(e.target.value)}
                    />
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowAddServiceModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Service
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
