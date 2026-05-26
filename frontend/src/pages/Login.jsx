import React, { useState } from 'react';
import { AuthService } from '../services/api';
import { ShieldCheck, Lock, User, Mail, UserCheck } from 'lucide-react';

export default function Login({ onLoginSuccess }) {
  const [isSignUp, setIsSignUp] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [role, setRole] = useState('ROLE_DOCTOR');
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMsg, setSuccessMsg] = useState(null);

  const handleSignIn = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMsg(null);

    try {
      const response = await AuthService.login(username, password);
      const data = response.data;

      // Store auth details in localStorage
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', data.username);
      localStorage.setItem('role', data.role);

      // Trigger app layout refresh
      onLoginSuccess();
    } catch (err) {
      console.error('Sign in error:', err);
      setError(
        err.response?.data || 
        'Invalid credentials. Please verify your username and password.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSignUp = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMsg(null);

    try {
      await AuthService.register(username, password, email, role);
      setSuccessMsg(`Account registered successfully! You can now log in as ${username}.`);
      
      // Auto toggle back to Login, retaining username and password for easy access
      setIsSignUp(false);
    } catch (err) {
      console.error('Sign up error:', err);
      setError(
        err.response?.data || 
        'Registration failed. Please make sure the username/email is not taken.'
      );
    } finally {
      setLoading(false);
    }
  };

  const toggleMode = () => {
    setIsSignUp(!isSignUp);
    setError(null);
    setSuccessMsg(null);
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

        <h1 className="login-logo">CurePulse HMS</h1>
        <p className="login-subtitle">
          {isSignUp ? 'Register New Staff Account' : 'Hospital Management System Portals'}
        </p>

        {error && <div className="login-error">{error}</div>}
        {successMsg && (
          <div style={{
            background: 'rgba(16, 185, 129, 0.15)',
            border: '1px solid rgba(16, 185, 129, 0.2)',
            color: 'var(--color-success)',
            padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-md)',
            fontSize: '0.85rem',
            marginBottom: '1.25rem',
            textAlign: 'center'
          }}>
            {successMsg}
          </div>
        )}

        {isSignUp ? (
          /* REGISTRATION FORM */
          <form onSubmit={handleSignUp}>
            <div className="form-group">
              <label>Username *</label>
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
                  placeholder="Choose unique username..."
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Email Address *</label>
              <div style={{ position: 'relative' }}>
                <span style={{
                  position: 'absolute',
                  left: '1rem',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)'
                }}>
                  <Mail size={18} />
                </span>
                <input
                  type="email"
                  className="form-control"
                  style={{ paddingLeft: '2.75rem' }}
                  placeholder="Choose your email..."
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Password *</label>
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
                  placeholder="Choose secure password..."
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: '2rem' }}>
              <label>Clinical Role / Title *</label>
              <div style={{ position: 'relative' }}>
                <span style={{
                  position: 'absolute',
                  left: '1rem',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)'
                }}>
                  <UserCheck size={18} />
                </span>
                <select
                  className="form-control"
                  style={{ paddingLeft: '2.75rem' }}
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                  required
                >
                  <option value="ROLE_DOCTOR">Clinical Specialist / Doctor</option>
                  <option value="ROLE_NURSE">Nursing & Care Staff</option>
                  <option value="ROLE_RECEPTIONIST">Front Desk & Reception</option>
                  <option value="ROLE_ADMIN">System Administrator</option>
                </select>
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary login-btn"
              disabled={loading}
            >
              {loading ? 'Creating Credentials...' : 'Register Account'}
            </button>

            <p style={{
              textAlign: 'center',
              marginTop: '1.5rem',
              fontSize: '0.85rem',
              color: 'var(--text-secondary)'
            }}>
              Already registered?{' '}
              <button
                type="button"
                onClick={toggleMode}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'var(--color-primary)',
                  fontWeight: '600',
                  cursor: 'pointer',
                  padding: 0
                }}
              >
                Sign In Instead
              </button>
            </p>
          </form>
        ) : (
          /* LOGIN FORM */
          <form onSubmit={handleSignIn}>
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

            <p style={{
              textAlign: 'center',
              marginTop: '1.5rem',
              fontSize: '0.85rem',
              color: 'var(--text-secondary)'
            }}>
              Need a new account?{' '}
              <button
                type="button"
                onClick={toggleMode}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'var(--color-primary)',
                  fontWeight: '600',
                  cursor: 'pointer',
                  padding: 0
                }}
              >
                Create Account
              </button>
            </p>
          </form>
        )}
      </div>
    </div>
  );
}
