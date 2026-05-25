import React, { useState } from 'react';
import { AuthService } from '../services/api';
import { ShieldCheck, Lock, User } from 'lucide-react';

export default function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await AuthService.login(username, password);
      const data = response.data;

      // Store auth state in localStorage
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', data.username);
      localStorage.setItem('role', data.role);

      // Trigger success callback to reload App layout
      onLoginSuccess();
    } catch (err) {
      console.error('Login error:', err);
      setError(
        err.response?.data || 
        'Invalid username or password. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '1rem' }}>
          <div style={{
            background: 'rgba(99, 102, 241, 0.15)',
            color: 'var(--color-primary)',
            padding: '1rem',
            borderRadius: 'var(--radius-md)'
          }}>
            <ShieldCheck size={36} />
          </div>
        </div>

        <h1 className="login-logo">MediCore HMS</h1>
        <p className="login-subtitle">Hospital Management System Portals</p>

        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Username</label>
            <div style={{ position: 'relative' }}>
              <span style={{
                position: 'absolute',
                left: '1rem',
                top: '50%',
                transform: 'translateY(-50%)',
                color: 'var(--text-muted)'
              }}>
                <User size={18} />
              </span>
              <input
                type="text"
                className="form-control"
                style={{ paddingLeft: '2.75rem' }}
                placeholder="Enter system username..."
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: '2rem' }}>
            <label>Password</label>
            <div style={{ position: 'relative' }}>
              <span style={{
                position: 'absolute',
                left: '1rem',
                top: '50%',
                transform: 'translateY(-50%)',
                color: 'var(--text-muted)'
              }}>
                <Lock size={18} />
              </span>
              <input
                type="password"
                className="form-control"
                style={{ paddingLeft: '2.75rem' }}
                placeholder="Enter secure password..."
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary login-btn"
            disabled={loading}
          >
            {loading ? 'Authenticating Credentials...' : 'Access Portal System'}
          </button>
        </form>
      </div>
    </div>
  );
}
