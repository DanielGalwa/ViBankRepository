import React, { useState, useEffect } from 'react';
import './Header.css'; 
import httpClient from "../httpClient";

const Header = () => {
  const [logo, setLogo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
        try {
            const response = await httpClient.get('http://localhost:8080/photos/logo.png', {
                responseType: 'blob',
            });
            const imageUrl = URL.createObjectURL(response.data);
            setLogo(imageUrl);
        } catch (err) {
            if (err.response) {
                setError(`Błąd: ${err.response.status} ${err.response.statusText}`);
            } else if (err.request) {
                setError('Nie można połączyć się z serwerem');
            } else {
                setError(err.message);
            }
        } finally {
            setLoading(false);
        }
    };

    fetchData();
}, []);


  if (loading) {
      return <p>Loading...</p>;
  }

  if (error) {
      return <p>Error: {error}</p>;
  }

  return (
      <header>
          <div>
            {logo && <img src={logo} alt="Logo" className='logo' />}
          </div>
          <div className="right-menu">
            <div className="row">
              <a href="/login" class="login-button"><span class="material-symbols-outlined">lock</span><span>Zaloguj się</span></a>
              {/* <a href="/register" class="account-open-button">Otwórz konto</a> */}
            </div>
            {/* <div className="row">
              <a href="/test" class="account-open-button">8888888888888888888888888888</a>
            </div> */}
          </div>
        </header>
  )
}

export default Header;
