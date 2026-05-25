import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Users, CalendarDays, ReceiptText, LogOut } from 'lucide-react';

export default function Sidebar() {
  const username = localStorage.getItem('username') || 'Admin';
  const role = localStorage.getItem('role') || 'ROLE_ADMIN';
  
  // Format details for presentation
  const initials = username.substring(0, 2).toUpperCase();
  const displayRole = role.replace('ROLE_', '').replace('_', ' ').toUpperCase();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    // Force a fresh reload to reset states
    window.location.reload();
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <span style={{ fontSize: '1.5rem' }}>🏥</span>
        <span>MediCore HMS</span>
      </div>

      <nav style={{ flex: 1 }}>
        <ul className="sidebar-menu">
          <li className="sidebar-item">
            <NavLink to="/" className={({ isActive }) => isActive ? 'active' : ''}>
              <LayoutDashboard size={18} />
              <span>Dashboard</span>
            </NavLink>
          </li>
          <li className="sidebar-item">
            <NavLink to="/patients" className={({ isActive }) => isActive ? 'active' : ''}>
              <Users size={18} />
              <span>Patients</span>
            </NavLink>
          </li>
          <li className="sidebar-item">
            <NavLink to="/appointments" className={({ isActive }) => isActive ? 'active' : ''}>
              <CalendarDays size={18} />
              <span>Appointments</span>
            </NavLink>
          </li>
          <li className="sidebar-item">
            <NavLink to="/billing" className={({ isActive }) => isActive ? 'active' : ''}>
              <ReceiptText size={18} />
              <span>Billing & Invoices</span>
            </NavLink>
          </li>
        </ul>
      </nav>

      <div className="sidebar-footer">
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          width: '100%'
        }}>
          <div className="sidebar-user">
            <div className="user-avatar">{initials}</div>
            <div className="user-info">
              <h4>{username}</h4>
              <p>{displayRole}</p>
            </div>
          </div>
          <button 
            onClick={handleLogout}
            title="Log Out of System"
            style={{
              background: 'transparent',
              border: 'none',
              color: 'var(--text-muted)',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              padding: '0.25rem',
              borderRadius: 'var(--radius-sm)',
              transition: 'color 0.15s, background-color 0.15s'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.color = 'var(--color-danger)';
              e.currentTarget.style.backgroundColor = 'rgba(239, 68, 68, 0.1)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.color = 'var(--text-muted)';
              e.currentTarget.style.backgroundColor = 'transparent';
            }}
          >
            <LogOut size={20} />
          </button>
        </div>
      </div>
    </aside>
  );
}
