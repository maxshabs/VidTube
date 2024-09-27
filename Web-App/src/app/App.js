// src/App.js
import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import './App.css';
import ManageRoutes from '../ManageRoutes';

function App() {
  return (
    <Router>
      <ManageRoutes />
    </Router>
  );
}

export default App;