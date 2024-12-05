import React, { useState, useEffect } from 'react';
import './HeaderLogged.css'; 
import { Link } from 'react-router-dom'; 

const HeaderLogged = () => {
  const [logo, setLogo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
      const fetchData = async () => {
          try {
              const response = await fetch('http://localhost:8080/photos/logo.png');
              if (!response.ok) {
                  throw new Error('Nieudało się wczytać obrazu');
              }
              const blob = await response.blob();
              const imageUrl = URL.createObjectURL(blob);
              setLogo(imageUrl);
          } catch (err) {
              setError(err.message);
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
        {/* <div>
            {logo && <img src={logo} alt="Logo" className='logo'/>}
        </div> */}
        <div>
            {logo && (
              <Link to="/dashboard"> {/* Link do /dashboard */}
                <img src={logo} alt="Logo" className='logo' />
              </Link>
            )}
        </div>
        <div>
            <a href="/logout" class="btn-outline-danger"><span class="material-symbols-outlined">lock</span><span>Wyloguj się</span></a>
         </div>
    </header>
  )
}

export default HeaderLogged;
