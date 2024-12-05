import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home'
import Offer from './pages/Offer'
import Login from './pages/Login'
import Test from './pages/Test'
import NotFound from './pages/NotFound';
import Logout from './pages/Logout'; 
import Dashboard from './pages/Dashboard';
import TransferFunds from './pages/TransferFunds';
import History from './pages/History';
import TwoFactoryAuth from './pages/TwoFactoryAuth';
import { SecurityProvider } from './components/SecurityContext';

function App() {
  return (
    <div className='container'>
      <SecurityProvider>
        <BrowserRouter>
          <div className='content'>
              <Routes>
                <Route index element={<Home />} />
                <Route path="/home" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/logout" element={<Logout />} /> 
                <Route path="/dashboard" element={<Dashboard />}/>
                <Route path="/history" element={<History />}/>
                <Route path="/transferFunds" element={<TransferFunds />}/>
                <Route path="/offer" element={<Offer />} />
                <Route path="/test" element={<Test />} />
                <Route path="/twofactoryauth" element={<TwoFactoryAuth />}/>
                <Route
                        path="*"
                        element={<NotFound />}
                    />
              </Routes>
          </div>
        </BrowserRouter>
      </SecurityProvider>
    </div>
  );
}

export default App;
