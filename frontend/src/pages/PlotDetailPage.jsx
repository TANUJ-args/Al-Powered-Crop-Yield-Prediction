import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { plotAPI, conditionAPI, predictionAPI, recommendationAPI } from '../services/api';
import { t } from '../i18n/translations';

export default function PlotDetailPage({ lang }) {
  const { plotId } = useParams();
  const [plot, setPlot] = useState(null);
  const [predictions, setPredictions] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [predicting, setPredicting] = useState(false);
  const [generatingRecs, setGeneratingRecs] = useState(false);
  const [showConditionForm, setShowConditionForm] = useState(false);
  const [condForm, setCondForm] = useState({
    rainfall: '', tempAvg: '', humidity: '', soilMoisture: '',
    soilPh: '', nitrogen: '', phosphorus: '', potassium: '',
  });

  useEffect(() => {
    loadData();
  }, [plotId]);

  const loadData = async () => {
    try {
      const [plotRes, predRes, recRes] = await Promise.all([
        plotAPI.getById(plotId),
        predictionAPI.getAll(plotId),
        recommendationAPI.getAll(plotId),
      ]);
      setPlot(plotRes.data);
      setPredictions(predRes.data);
      setRecommendations(recRes.data);
    } catch (err) {
      console.error('Failed to load plot data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePredict = async () => {
    setPredicting(true);
    try {
      const res = await predictionAPI.predict(plotId);
      setPredictions(prev => [res.data, ...prev]);
    } catch (err) {
      console.error('Prediction failed:', err);
    } finally {
      setPredicting(false);
    }
  };

  const handleGetRecommendations = async () => {
    setGeneratingRecs(true);
    try {
      const res = await recommendationAPI.generate(plotId);
      setRecommendations(res.data);
    } catch (err) {
      console.error('Recommendations failed:', err);
    } finally {
      setGeneratingRecs(false);
    }
  };

  const handleConditionSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {};
      Object.entries(condForm).forEach(([key, val]) => {
        if (val !== '') payload[key] = parseFloat(val);
      });
      await conditionAPI.add(plotId, payload);
      setShowConditionForm(false);
      setCondForm({
        rainfall: '', tempAvg: '', humidity: '', soilMoisture: '',
        soilPh: '', nitrogen: '', phosphorus: '', potassium: '',
      });
    } catch (err) {
      console.error('Failed to add conditions:', err);
    }
  };

  if (loading) return <div className="loading"><div className="spinner" /></div>;
  if (!plot) return <div className="alert alert-error">Plot not found</div>;

  return (
    <div>
      <Link to={`/farms/${plot.farmId}`} style={{ fontSize: '0.9rem' }}>← {plot.farmName}</Link>

      {/* Plot Header */}
      <div className="card" style={{ marginTop: '1rem' }}>
        <span className="crop-badge" style={{ fontSize: '1rem' }}>{plot.cropType}</span>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', marginTop: '0.8rem' }}>
          <div><strong>{t('area', lang)}:</strong> {plot.area} ha</div>
          <div><strong>{t('season', lang)}:</strong> {plot.season || '–'}</div>
          <div><strong>{t('sowingDate', lang)}:</strong> {plot.sowingDate || '–'}</div>
          <div><strong>Farm:</strong> {plot.farmName}</div>
        </div>

        <div className="plot-actions">
          <button className="btn btn-primary" onClick={handlePredict} disabled={predicting}>
            {predicting ? '⏳ ...' : `🔮 ${t('predictYield', lang)}`}
          </button>
          <button className="btn btn-secondary" onClick={handleGetRecommendations} disabled={generatingRecs}>
            {generatingRecs ? '⏳ ...' : `💡 ${t('getRecommendations', lang)}`}
          </button>
          <button className="btn btn-sm" style={{ background: '#636e72', color: 'white' }}
                  onClick={() => setShowConditionForm(!showConditionForm)}>
            📊 {t('addConditions', lang)}
          </button>
        </div>
      </div>

      {/* Add Field Conditions Form */}
      {showConditionForm && (
        <div className="card">
          <h3>{t('addConditions', lang)}</h3>
          <form onSubmit={handleConditionSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>{t('rainfall', lang)}</label>
                <input type="number" step="any" value={condForm.rainfall}
                       onChange={e => setCondForm({...condForm, rainfall: e.target.value})} />
              </div>
              <div className="form-group">
                <label>{t('temperature', lang)}</label>
                <input type="number" step="any" value={condForm.tempAvg}
                       onChange={e => setCondForm({...condForm, tempAvg: e.target.value})} />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>{t('humidity', lang)}</label>
                <input type="number" step="any" value={condForm.humidity}
                       onChange={e => setCondForm({...condForm, humidity: e.target.value})} />
              </div>
              <div className="form-group">
                <label>{t('soilMoisture', lang)}</label>
                <input type="number" step="any" value={condForm.soilMoisture}
                       onChange={e => setCondForm({...condForm, soilMoisture: e.target.value})} />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>{t('soilPh', lang)}</label>
                <input type="number" step="any" value={condForm.soilPh}
                       onChange={e => setCondForm({...condForm, soilPh: e.target.value})} />
              </div>
              <div className="form-group">
                <label>{t('nitrogen', lang)}</label>
                <input type="number" step="any" value={condForm.nitrogen}
                       onChange={e => setCondForm({...condForm, nitrogen: e.target.value})} />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>{t('phosphorus', lang)}</label>
                <input type="number" step="any" value={condForm.phosphorus}
                       onChange={e => setCondForm({...condForm, phosphorus: e.target.value})} />
              </div>
              <div className="form-group">
                <label>{t('potassium', lang)}</label>
                <input type="number" step="any" value={condForm.potassium}
                       onChange={e => setCondForm({...condForm, potassium: e.target.value})} />
              </div>
            </div>
            <button type="submit" className="btn btn-primary">{t('submit', lang)}</button>
          </form>
        </div>
      )}

      {/* Latest Prediction */}
      {predictions.length > 0 && (
        <div className="card">
          <h3>{t('predictedYield', lang)}</h3>
          <div className="prediction-result">
            <div className="yield-value">{predictions[0].predictedYield}</div>
            <div className="yield-unit">{t('tonnesPerHa', lang)}</div>
            <div style={{ marginTop: '0.5rem', fontSize: '0.85rem', color: 'var(--text-light)' }}>
              Model: {predictions[0].modelVersion} •{' '}
              {new Date(predictions[0].predictionDate).toLocaleString()}
            </div>
          </div>
        </div>
      )}

      {/* Prediction History */}
      {predictions.length > 1 && (
        <div className="card">
          <h3>{t('predictions', lang)} ({predictions.length})</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '0.5rem' }}>
            <thead>
              <tr style={{ borderBottom: '2px solid var(--border)', textAlign: 'left' }}>
                <th style={{ padding: '0.5rem' }}>Date</th>
                <th style={{ padding: '0.5rem' }}>Yield</th>
                <th style={{ padding: '0.5rem' }}>Model</th>
              </tr>
            </thead>
            <tbody>
              {predictions.map(p => (
                <tr key={p.id} style={{ borderBottom: '1px solid var(--border)' }}>
                  <td style={{ padding: '0.5rem' }}>{new Date(p.predictionDate).toLocaleDateString()}</td>
                  <td style={{ padding: '0.5rem', fontWeight: 600 }}>{p.predictedYield} t/ha</td>
                  <td style={{ padding: '0.5rem', color: 'var(--text-light)' }}>{p.modelVersion}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Recommendations */}
      <div className="card">
        <h3>{t('recommendations', lang)} ({recommendations.length})</h3>
        {recommendations.length === 0 ? (
          <p style={{ color: 'var(--text-light)', marginTop: '0.5rem' }}>{t('noRecommendations', lang)}</p>
        ) : (
          <div className="recommendation-list" style={{ marginTop: '0.5rem' }}>
            {recommendations.map(rec => (
              <div key={rec.id} className={`card rec-card ${rec.type}`}>
                <div className="rec-type">
                  {rec.type === 'IRRIGATION' ? '💧' : rec.type === 'FERTILIZER' ? '🧪' : '🐛'}{' '}
                  {t(rec.type.toLowerCase(), lang)}
                </div>
                <p>{rec.text}</p>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-light)', marginTop: '0.3rem' }}>
                  {rec.date} • {rec.ruleSource}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
