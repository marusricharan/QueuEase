import React from 'react';
import { User, HeartPulse, Gamepad2, Scissors, Wrench, Dog, Shield, LogIn } from 'lucide-react';

export default function DemoSwitcher({ currentUser, onSwitchUser, onOpenAuth }) {
  const demoUsers = [
    {
      label: 'Sarah (Customer)',
      email: 'sarah@example.com',
      password: 'customer123',
      role: 'ROLE_CUSTOMER',
      icon: User,
    },
    {
      label: 'Dr. Maya (Clinic Staff)',
      email: 'clinic_staff@queueease.com',
      password: 'staff123',
      role: 'ROLE_STAFF',
      icon: HeartPulse,
    },
    {
      label: 'Leo (Play Area Staff)',
      email: 'play_staff@queueease.com',
      password: 'staff123',
      role: 'ROLE_STAFF',
      icon: Gamepad2,
    },
    {
      label: 'Alex (Salon Staff)',
      email: 'salon_staff@queueease.com',
      password: 'staff123',
      role: 'ROLE_STAFF',
      icon: Scissors,
    },
    {
      label: 'Marcus (Auto Repair)',
      email: 'auto_staff@queueease.com',
      password: 'staff123',
      role: 'ROLE_STAFF',
      icon: Wrench,
    },
    {
      label: 'Emma (Pet Spa Staff)',
      email: 'pet_staff@queueease.com',
      password: 'staff123',
      role: 'ROLE_STAFF',
      icon: Dog,
    },
    {
      label: 'Admin (System)',
      email: 'admin@queueease.com',
      password: 'admin123',
      role: 'ROLE_ADMIN',
      icon: Shield,
    },
  ];

  return (
    <div className="demo-strip">
      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
        <span style={{ fontWeight: 600, color: 'var(--text-main)' }}>Demo Switcher:</span>
        <span style={{ color: 'var(--text-muted)' }}>1-click login across small business roles</span>
      </div>

      <div className="demo-buttons">
        {demoUsers.map((u) => {
          const Icon = u.icon;
          const isActive = currentUser && currentUser.email === u.email;
          return (
            <button
              key={u.email}
              className={`demo-btn ${isActive ? 'active-role' : ''}`}
              onClick={() => onSwitchUser(u.email, u.password)}
              title={`Switch instantly to ${u.label}`}
            >
              <Icon size={13} style={{ color: isActive ? 'var(--primary)' : 'var(--text-muted)' }} />
              <span>{u.label}</span>
              {isActive && <span style={{ fontSize: '0.7rem', color: '#16a34a', fontWeight: 700 }}>●</span>}
            </button>
          );
        })}

        <button
          className="demo-btn"
          style={{ background: '#f8fafc' }}
          onClick={() => onOpenAuth('login')}
        >
          <LogIn size={13} style={{ color: 'var(--text-muted)' }} />
          <span>Other Account</span>
        </button>
      </div>
    </div>
  );
}
