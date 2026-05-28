import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'

// ⚠️ SYSTEM CRASH BUG (To fix this crash, simply comment out or delete the throw statement below)
throw new Error("CRITICAL_CRASH: Simulated system failure at frontend entrypoint.");

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)

