import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { plotAPI } from '../services/api';
import { t } from '../i18n/translations';

const CROPS = ['Rice', 'Wheat', 'Maize', 'Sugarcane', 'Cotton', 'Soybean', 'Groundnut'];
const SEASONS = ['Kharif', 'Rabi', 'Zaid'];

export default function AddPlotPage({ lang }) {
  const { farmId } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    cropType: '', area: '', season: '', sowingDate: '',
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
      await plotAPI.create({
        farmId: parseInt(farmId),
        cropType: form.cropType,
        area: parseFloat(form.area),
        season: form.season,
        sowingDate: form.sowingDate || null,
      });
      navigate(`/farms/${farmId}`);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create plot');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '0 auto' }}>
      <div className="card">
        <h2>{t('addPlot', lang)}</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>{t('cropType', lang)} *</label>
            <select name="cropType" value={form.cropType} onChange={handleChange} required>
              <option value="">-- Select Crop --</option>
              {CROPS.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>{t('area', lang)} *</label>
              <input name="area" type="number" step="0.1" min="0.1" value={form.area}
                     onChange={handleChange} placeholder="e.g. 2.5" required />
            </div>
            <div className="form-group">
              <label>{t('season', lang)}</label>
              <select name="season" value={form.season} onChange={handleChange}>
                <option value="">-- Select --</option>
                {SEASONS.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          </div>
          <div className="form-group">
            <label>{t('sowingDate', lang)}</label>
            <input name="sowingDate" type="date" value={form.sowingDate} onChange={handleChange} />
          </div>
          <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? t('loading', lang) : t('submit', lang)}
            </button>
            <button type="button" className="btn btn-secondary"
                    onClick={() => navigate(`/farms/${farmId}`)}>
              {t('cancel', lang)}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
