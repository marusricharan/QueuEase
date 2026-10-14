import React, { useState, useEffect, useCallback } from 'react';
import Navbar from './components/Navbar';
import DemoSwitcher from './components/DemoSwitcher';
import AuthModal from './components/AuthModal';
import BusinessModal from './components/BusinessModal';
import CustomerExploreView from './views/CustomerExploreView';
import CustomerBookingsView from './views/CustomerBookingsView';
import CustomerHistoryView from './views/CustomerHistoryView';
import StaffDashboardView from './views/StaffDashboardView';
import AdminDashboardView from './views/AdminDashboardView';
import { api, getUser, setUser, getToken, setToken } from './api';

export default function App() {
  const [currentUser, setCurrentUserState] = useState(getUser);
  const [activeTab, setActiveTab] = useState('explore'); // 'explore', 'my-bookings', 'my-history', 'staff-dashboard', 'admin'

  // Businesses
  const [businesses, setBusinesses] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedBusiness, setSelectedBusiness] = useState(null);

  // Active Queue Ticket (for logged in customer)
  const [activeTicket, setActiveTicket] = useState(null);

  // Customer Data
  const [myBookings, setMyBookings] = useState([]);
  const [myHistory, setMyHistory] = useState([]);
  const [bookingsLoading, setBookingsLoading] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(false);

  // Staff
  const [staffBusinessId, setStaffBusinessId] = useState(null);

  // Auth Modal
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [authModalMode, setAuthModalMode] = useState('login');

  const categories = ['All', 'Clinics & Health', 'Play Areas & Kids', 'Salons & Grooming', 'Auto & Repairs', 'Pet Care'];

  // Load businesses
  const loadBusinesses = useCallback(async () => {
    try {
      let data = [];
      if (searchQuery.trim()) {
        data = await api.searchBusinesses(searchQuery);
      } else if (selectedCategory !== 'All') {
        data = await api.getBusinessesByCategory(selectedCategory);
      } else {
        data = await api.getBusinesses();
      }
      setBusinesses(data || []);
    } catch (e) {
      console.warn('Failed loading businesses:', e);
    }
  }, [searchQuery, selectedCategory]);

  // Load active ticket for current customer
  const checkActiveTicket = useCallback(async () => {
    if (!currentUser) {
      setActiveTicket(null);
      return;
    }

    try {
      // Check customer active tickets across businesses
      const hist = await api.getMyQueueHistory();
      const active = hist?.find(
        (h) => h.status === 'WAITING' || h.status === 'CALLED' || h.status === 'IN_SERVICE'
      );
      setActiveTicket(active || null);
    } catch (e) {
      // User might be unauthenticated
      setActiveTicket(null);
    }
  }, [currentUser]);

  // Polling effect (updates every 4 seconds)
  useEffect(() => {
    loadBusinesses();
    const interval = setInterval(() => {
      loadBusinesses();
      if (currentUser) {
        checkActiveTicket();
      }
    }, 4000);
    return () => clearInterval(interval);
  }, [loadBusinesses, checkActiveTicket, currentUser]);

  // Customer bookings
  const loadCustomerBookings = useCallback(async () => {
    if (!currentUser) return;
    setBookingsLoading(true);
    try {
      const data = await api.getMyBookings();
      setMyBookings(data || []);
    } catch (e) {
      console.warn('Failed loading bookings:', e);
    } finally {
      setBookingsLoading(false);
    }
  }, [currentUser]);

  // Customer history
  const loadCustomerHistory = useCallback(async () => {
    if (!currentUser) return;
    setHistoryLoading(true);
    try {
      const data = await api.getMyQueueHistory();
      setMyHistory(data || []);
    } catch (e) {
      console.warn('Failed loading history:', e);
    } finally {
      setHistoryLoading(false);
    }
  }, [currentUser]);

  useEffect(() => {
    if (activeTab === 'my-bookings') {
      loadCustomerBookings();
    } else if (activeTab === 'my-history') {
      loadCustomerHistory();
    }
  }, [activeTab, loadCustomerBookings, loadCustomerHistory]);

  // Handle Demo User Switching
  async function handleSwitchDemoUser(email, password) {
    try {
      const res = await api.login({ email, password });
      setToken(res.token);
      setUser(res);
      setCurrentUserState(res);

      if (res.role === 'ROLE_STAFF') {
        setStaffBusinessId(res.businessId || 1);
        setActiveTab('staff-dashboard');
      } else if (res.role === 'ROLE_ADMIN') {
        setActiveTab('admin');
      } else {
        setActiveTab('explore');
      }
      checkActiveTicket();
    } catch (err) {
      alert(`Demo login error: ${err.message}`);
    }
  }

  function handleLogout() {
    setToken(null);
    setUser(null);
    setCurrentUserState(null);
    setActiveTicket(null);
    setActiveTab('explore');
  }

  function handleAuthSuccess(userData) {
    setCurrentUserState(userData);
    if (userData.role === 'ROLE_STAFF') {
      setStaffBusinessId(userData.businessId || 1);
      setActiveTab('staff-dashboard');
    } else if (userData.role === 'ROLE_ADMIN') {
      setActiveTab('admin');
    } else {
      setActiveTab('explore');
    }
    checkActiveTicket();
  }

  async function handleLeaveQueue(ticketId) {
    try {
      await api.leaveQueue(ticketId);
      setActiveTicket(null);
      loadBusinesses();
      loadCustomerHistory();
    } catch (err) {
      alert(err.message || 'Failed to leave queue');
    }
  }

  async function handleCancelBooking(bookingId) {
    try {
      await api.cancelBooking(bookingId);
      loadCustomerBookings();
    } catch (err) {
      alert(err.message || 'Failed to cancel appointment');
    }
  }

  return (
    <div className="app-container">
      {/* Instant Demo Switcher Strip */}
      <DemoSwitcher
        currentUser={currentUser}
        onSwitchUser={handleSwitchDemoUser}
        onOpenAuth={(mode) => {
          setAuthModalMode(mode);
          setAuthModalOpen(true);
        }}
      />

      {/* Navigation Bar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currentUser={currentUser}
        onLogout={handleLogout}
        onOpenAuth={(mode) => {
          setAuthModalMode(mode);
          setAuthModalOpen(true);
        }}
      />

      {/* Main View Area */}
      <main className="main-content">
        {activeTab === 'explore' && (
          <CustomerExploreView
            businesses={businesses}
            categories={categories}
            selectedCategory={selectedCategory}
            setSelectedCategory={setSelectedCategory}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
            onSelectBusiness={(b) => setSelectedBusiness(b)}
            activeTicket={activeTicket}
            onLeaveQueue={handleLeaveQueue}
          />
        )}

        {activeTab === 'my-bookings' && (
          <CustomerBookingsView
            bookings={myBookings}
            onCancelBooking={handleCancelBooking}
            loading={bookingsLoading}
          />
        )}

        {activeTab === 'my-history' && (
          <CustomerHistoryView
            history={myHistory}
            loading={historyLoading}
          />
        )}

        {activeTab === 'staff-dashboard' && (
          <StaffDashboardView
            businessId={staffBusinessId}
            businesses={businesses}
            onChangeBusiness={(id) => setStaffBusinessId(id)}
            currentUser={currentUser}
          />
        )}

        {activeTab === 'admin' && (
          <AdminDashboardView />
        )}
      </main>

      {/* Modals */}
      <AuthModal
        isOpen={authModalOpen}
        initialMode={authModalMode}
        onClose={() => setAuthModalOpen(false)}
        onAuthSuccess={handleAuthSuccess}
      />

      {selectedBusiness && (
        <BusinessModal
          business={selectedBusiness}
          currentUser={currentUser}
          onClose={() => setSelectedBusiness(null)}
          onQueueJoined={(entry) => {
            setActiveTicket(entry);
            loadBusinesses();
          }}
          onBookingCreated={() => {
            loadCustomerBookings();
          }}
          onRequireAuth={() => {
            setSelectedBusiness(null);
            setAuthModalMode('login');
            setAuthModalOpen(true);
          }}
        />
      )}
    </div>
  );
}
