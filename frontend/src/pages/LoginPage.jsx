import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import { t } from '../i18n/translations';

export default function LoginPage({ lang }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await authAPI.login({ email, password });
      login(res.data);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="card auth-card">
        <h2>🌾 {t('login', lang)}</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>{t('email', lang)}</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                   placeholder="farmer@example.com" required />
          </div>
          <div className="form-group">
            <label>{t('password', lang)}</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)}
                   placeholder="••••••" required />
          </div>
          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? t('loading', lang) : t('login', lang)}
          </button>
        </form>
        <div className="auth-footer">
          {t('noAccount', lang)} <Link to="/register">{t('register', lang)}</Link>
        </div>
      </div>
    </div>
  );
}
