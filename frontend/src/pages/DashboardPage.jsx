import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { farmAPI } from '../services/api';
import { t } from '../i18n/translations';

export default function DashboardPage({ lang }) {
  const { user } = useAuth();
  const [farms, setFarms] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadFarms();
  }, []);

  const loadFarms = async () => {
    try {
      const res = await farmAPI.getAll();
      setFarms(res.data);
    } catch (err) {
      console.error('Failed to load farms:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading"><div className="spinner" /></div>;

  return (
    <div>
      <div className="dashboard-header">
        <h2>{t('welcome', lang)}, {user?.name}!</h2>
        <Link to="/add-farm" className="btn btn-primary">{t('addFarm', lang)}</Link>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-value">{farms.length}</div>
          <div className="stat-label">{t('totalFarms', lang)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">–</div>
          <div className="stat-label">{t('totalPlots', lang)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">–</div>
          <div className="stat-label">{t('latestYield', lang)}</div>
        </div>
      </div>

      {/* Farms list */}
      <h3 style={{ marginBottom: '1rem' }}>{t('myFarms', lang)}</h3>
      {farms.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ fontSize: '1.1rem', color: 'var(--text-light)' }}>{t('noFarms', lang)}</p>
          <Link to="/add-farm" className="btn btn-primary" style={{ marginTop: '1rem', display: 'inline-block' }}>
            {t('addFarm', lang)}
          </Link>
        </div>
      ) : (
        <div className="farm-grid">
          {farms.map(farm => (
            <Link to={`/farms/${farm.id}`} key={farm.id} style={{ textDecoration: 'none', color: 'inherit' }}>
              <div className="card farm-card">
                <h3>{farm.name}</h3>
                <div className="farm-meta">
                  {farm.region && <span>📍 {farm.region}</span>}
                  {farm.soilType && <span>🌍 {farm.soilType}</span>}
                  {farm.location && <span>📌 {farm.location}</span>}
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
