import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { farmAPI, plotAPI } from '../services/api';
import { t } from '../i18n/translations';

export default function FarmDetailPage({ lang }) {
  const { farmId } = useParams();
  const [farm, setFarm] = useState(null);
  const [plots, setPlots] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, [farmId]);

  const loadData = async () => {
    try {
      const [farmRes, plotsRes] = await Promise.all([
        farmAPI.getById(farmId),
        plotAPI.getByFarm(farmId),
      ]);
      setFarm(farmRes.data);
      setPlots(plotsRes.data);
    } catch (err) {
      console.error('Failed to load farm data:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading"><div className="spinner" /></div>;
  if (!farm) return <div className="alert alert-error">Farm not found</div>;

  return (
    <div>
      <div className="dashboard-header">
        <div>
          <Link to="/" style={{ fontSize: '0.9rem' }}>← {t('dashboard', lang)}</Link>
          <h2 style={{ marginTop: '0.5rem' }}>{farm.name}</h2>
          <div className="farm-meta" style={{ marginTop: '0.3rem' }}>
            {farm.region && <span>📍 {farm.region}</span>}
            {farm.soilType && <span>🌍 {farm.soilType}</span>}
            {farm.location && <span>📌 {farm.location}</span>}
          </div>
        </div>
        <Link to={`/farms/${farmId}/add-plot`} className="btn btn-primary">
          {t('addPlot', lang)}
        </Link>
      </div>

      <h3 style={{ marginBottom: '1rem' }}>{t('plots', lang)} ({plots.length})</h3>

      {plots.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ color: 'var(--text-light)' }}>{t('noPlots', lang)}</p>
        </div>
      ) : (
        <div className="plot-list">
          {plots.map(plot => (
            <Link to={`/plots/${plot.id}`} key={plot.id}
                  style={{ textDecoration: 'none', color: 'inherit' }}>
              <div className="card plot-card">
                <span className="crop-badge">{plot.cropType}</span>
                <div style={{ marginTop: '0.5rem' }}>
                  <div><strong>{t('area', lang)}:</strong> {plot.area} ha</div>
                  <div><strong>{t('season', lang)}:</strong> {plot.season || '–'}</div>
                  <div><strong>{t('sowingDate', lang)}:</strong> {plot.sowingDate || '–'}</div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
