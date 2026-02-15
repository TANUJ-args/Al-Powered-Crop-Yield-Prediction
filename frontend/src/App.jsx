import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { t, languages } from './i18n/translations';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import FarmDetailPage from './pages/FarmDetailPage';
import PlotDetailPage from './pages/PlotDetailPage';
import AddFarmPage from './pages/AddFarmPage';
import AddPlotPage from './pages/AddPlotPage';

function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();
  if (loading) return <div className="loading"><div className="spinner" /></div>;
  return isAuthenticated ? children : <Navigate to="/login" />;
}

function Navbar({ lang, setLang }) {
  const { user, logout, isAuthenticated } = useAuth();
  const currentLangObj = languages.find(l => l.code === lang);
  const nextLang = languages.find(l => l.code !== lang);

  return (
    <nav className="navbar">
      <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>
        <h1>🌾 {t('appName', lang)}</h1>
      </Link>
      <div className="navbar-links">
        {isAuthenticated && (
          <>
            <Link to="/">{t('dashboard', lang)}</Link>
            <Link to="/add-farm">{t('addFarm', lang)}</Link>
            <span style={{ color: 'rgba(255,255,255,0.7)', fontSize: '0.9rem' }}>
              {user?.name}
            </span>
            <button onClick={logout}>{t('logout', lang)}</button>
          </>
        )}
        <button className="lang-toggle" onClick={() => setLang(nextLang.code)}>
          {nextLang.label}
        </button>
      </div>
    </nav>
  );
}

function AppContent() {
  const [lang, setLang] = useState('en');

  return (
    <BrowserRouter>
      <div className="app-container">
        <Navbar lang={lang} setLang={setLang} />
        <main className="main-content">
          <Routes>
            <Route path="/login" element={<LoginPage lang={lang} />} />
            <Route path="/register" element={<RegisterPage lang={lang} />} />
            <Route path="/" element={
              <ProtectedRoute><DashboardPage lang={lang} /></ProtectedRoute>
            } />
            <Route path="/add-farm" element={
              <ProtectedRoute><AddFarmPage lang={lang} /></ProtectedRoute>
            } />
            <Route path="/farms/:farmId" element={
              <ProtectedRoute><FarmDetailPage lang={lang} /></ProtectedRoute>
            } />
            <Route path="/farms/:farmId/add-plot" element={
              <ProtectedRoute><AddPlotPage lang={lang} /></ProtectedRoute>
            } />
            <Route path="/plots/:plotId" element={
              <ProtectedRoute><PlotDetailPage lang={lang} /></ProtectedRoute>
            } />
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
