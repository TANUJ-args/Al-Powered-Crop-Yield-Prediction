import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { farmAPI } from '../services/api';
import { t } from '../i18n/translations';

const SOIL_TYPES = ['Alluvial', 'Black', 'Red', 'Laterite', 'Sandy', 'Clay'];
const STATES = [
  'Andhra Pradesh', 'Punjab', 'Uttar Pradesh', 'Maharashtra', 'Karnataka',
  'Tamil Nadu', 'Madhya Pradesh', 'Rajasthan', 'Gujarat', 'West Bengal',
  'Telangana', 'Bihar', 'Odisha', 'Kerala', 'Haryana',
];

export default function AddFarmPage({ lang }) {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: '', location: '', latitude: '', longitude: '',
    region: '', soilType: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const payload = {
        ...form,
        latitude: form.latitude ? parseFloat(form.latitude) : null,
        longitude: form.longitude ? parseFloat(form.longitude) : null,
      };
      await farmAPI.create(payload);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create farm');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <div className="card">
        <h2>{t('addFarm', lang)}</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>{t('farmName', lang)} *</label>
            <input name="name" value={form.name} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>{t('location', lang)}</label>
            <input name="location" value={form.location} onChange={handleChange}
                   placeholder="Village, District" />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>{t('latitude', lang)}</label>
              <input name="latitude" type="number" step="any" value={form.latitude}
                     onChange={handleChange} placeholder="e.g. 17.385" />
            </div>
            <div className="form-group">
              <label>{t('longitude', lang)}</label>
              <input name="longitude" type="number" step="any" value={form.longitude}
                     onChange={handleChange} placeholder="e.g. 78.486" />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>{t('region', lang)}</label>
              <select name="region" value={form.region} onChange={handleChange}>
                <option value="">-- Select --</option>
                {STATES.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>{t('soilType', lang)}</label>
              <select name="soilType" value={form.soilType} onChange={handleChange}>
                <option value="">-- Select --</option>
                {SOIL_TYPES.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          </div>
          <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? t('loading', lang) : t('submit', lang)}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/')}>
              {t('cancel', lang)}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
