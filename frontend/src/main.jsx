import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { isPublicDemo } from './api/publicDemo.js'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {isPublicDemo && <div className="public-demo-banner" role="status">DEMO · Testovací e-shop — bez skutečných objednávek a plateb</div>}
    <App />
  </StrictMode>,
)
