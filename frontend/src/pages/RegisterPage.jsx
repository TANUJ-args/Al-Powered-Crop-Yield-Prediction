import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import { t } from '../i18n/translations';

export default function RegisterPage({ lang }) {
  const [form, setForm] = useState({ name: '', email: '', phone: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await authAPI.register({ ...form, role: 'FARMER' });
      login(res.data);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="card auth-card">
        <h2>🌾 {t('register', lang)}</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>{t('name', lang)}</label>
            <input name="name" value={form.name} onChange={handleChange}
                   placeholder="Ravi Kumar" required />
          </div>
          <div className="form-group">
            <label>{t('email', lang)}</label>
            <input type="email" name="email" value={form.email} onChange={handleChange}
                   placeholder="farmer@example.com" required />
          </div>
          <div className="form-group">
            <label>{t('phone', lang)}</label>
            <input name="phone" value={form.phone} onChange={handleChange}
                   placeholder="+91 9876543210" />
          </div>
          <div className="form-group">
            <label>{t('password', lang)}</label>
            <input type="password" name="password" value={form.password} onChange={handleChange}
                   placeholder="Minimum 6 characters" required minLength={6} />
          </div>
          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? t('loading', lang) : t('register', lang)}
          </button>
        </form>
        <div className="auth-footer">
          {t('haveAccount', lang)} <Link to="/login">{t('login', lang)}</Link>
        </div>
      </div>
    </div>
  );
}
