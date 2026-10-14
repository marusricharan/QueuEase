// QueueEase Central API Client

const BASE_URL = '/api';

export function getToken() {
  return localStorage.getItem('queueease_token');
}

export function setToken(token) {
  if (token) {
    localStorage.setItem('queueease_token', token);
  } else {
    localStorage.removeItem('queueease_token');
  }
}

export function getUser() {
  const u = localStorage.getItem('queueease_user');
  try {
    return u ? JSON.parse(u) : null;
  } catch {
    return null;
  }
}

export function setUser(user) {
  if (user) {
    localStorage.setItem('queueease_user', JSON.stringify(user));
  } else {
    localStorage.removeItem('queueease_user');
  }
}

async function request(endpoint, options = {}) {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    const errorMsg = data?.message || `Request failed with status ${response.status}`;
    throw new Error(errorMsg);
  }

  return data;
}

export const api = {
  // Auth
  login: (credentials) => request('/auth/login', { method: 'POST', body: JSON.stringify(credentials) }),
  register: (details) => request('/auth/register', { method: 'POST', body: JSON.stringify(details) }),
  getMe: () => request('/auth/me'),

  // Businesses
  getBusinesses: () => request('/businesses'),
  searchBusinesses: (query) => request(`/businesses/search?q=${encodeURIComponent(query)}`),
  getBusinessesByCategory: (category) => request(`/businesses/category/${encodeURIComponent(category)}`),
  getBusinessById: (id) => request(`/businesses/${id}`),
  updateBusiness: (id, data) => request(`/businesses/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  addService: (businessId, service) => request(`/businesses/${businessId}/services`, { method: 'POST', body: JSON.stringify(service) }),
  updateService: (serviceId, service) => request(`/businesses/services/${serviceId}`, { method: 'PUT', body: JSON.stringify(service) }),
  deleteService: (serviceId) => request(`/businesses/services/${serviceId}`, { method: 'DELETE' }),

  // Queue (Customer)
  joinQueue: (data) => request('/queue/join', { method: 'POST', body: JSON.stringify(data) }),
  leaveQueue: (id) => request(`/queue/${id}/leave`, { method: 'POST' }),
  getActiveQueueEntry: (businessId) => request(`/queue/business/${businessId}/active`),
  getMyQueueHistory: () => request('/queue/my-history'),
  getPublicQueue: (businessId) => request(`/queue/business/${businessId}/public`),

  // Queue (Staff)
  getStaffQueue: (businessId) => request(`/staff/queue/business/${businessId}`),
  getStaffCompletedToday: (businessId) => request(`/staff/queue/business/${businessId}/completed`),
  callNext: (businessId, counterNumber = 1) => request(`/staff/queue/business/${businessId}/call-next?counterNumber=${counterNumber}`, { method: 'POST' }),
  updateQueueStatus: (id, data) => request(`/staff/queue/${id}/status`, { method: 'PATCH', body: JSON.stringify(data) }),
  addWalkIn: (businessId, data) => request(`/staff/queue/business/${businessId}/walk-in`, { method: 'POST', body: JSON.stringify(data) }),
  getStaffAnalytics: (businessId, date) => request(`/staff/business/${businessId}/analytics${date ? `?date=${date}` : ''}`),

  // Bookings
  createBooking: (data) => request('/bookings', { method: 'POST', body: JSON.stringify(data) }),
  getMyBookings: () => request('/bookings/my-bookings'),
  getBusinessBookings: (businessId) => request(`/bookings/business/${businessId}`),
  updateBookingStatus: (id, data) => request(`/bookings/${id}/status`, { method: 'PATCH', body: JSON.stringify(data) }),
  cancelBooking: (id) => request(`/bookings/${id}/cancel`, { method: 'POST' }),

  // Reviews
  getBusinessReviews: (businessId) => request(`/reviews/business/${businessId}`),
  addReview: (businessId, data) => request(`/reviews/business/${businessId}`, { method: 'POST', body: JSON.stringify(data) }),

  // Admin
  getAdminUsers: () => request('/admin/users'),
  toggleUserActive: (id) => request(`/admin/users/${id}/toggle-active`, { method: 'PATCH' }),
  getAdminBusinesses: () => request('/admin/businesses'),
  toggleBusinessActive: (id) => request(`/admin/businesses/${id}/toggle-active`, { method: 'PATCH' }),
  getAdminStats: () => request('/admin/stats'),
};
