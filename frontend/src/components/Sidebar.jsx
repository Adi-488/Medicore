import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Users, CalendarDays, ReceiptText, LogOut } from 'lucide-react';

export default function Sidebar() {
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
        <div className="sidebar-user">
          <div className="user-avatar">AD</div>
          <div className="user-info">
            <h4>Dr. Aditya</h4>
            <p>System Administrator</p>
          </div>
        </div>
      </div>
    </aside>
  );
}
