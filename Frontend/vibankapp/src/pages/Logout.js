import React, {useEffect } from 'react';
// import { AuthContext } from '../components/AuthContext';
import { useSecurityContext } from '../components/SecurityContext';
import { useNavigate } from 'react-router-dom';
import httpClient from "../httpClient";


function Logout() {
    // const { logout } = useContext(AuthContext);
    const { getCookie } = useSecurityContext(); 
    const navigate = useNavigate();

    useEffect(() => {
        const logOut = async () => {
            try {
                const csrfToken = getCookie("CSRFcookie");
                await httpClient.post('http://localhost:8080/auth/logout', {},{
                    headers: {
                        'X-CSRF-Token': csrfToken
                    }
                });
                document.cookie = "CSRFcookie=; path=/; max-age=0;";

                navigate("/home");
            } catch (error) {
                alert("Nieudana próba wylogowania");
            }
        };

        logOut();
    }, [navigate, getCookie]);

    return (
        <div>
            <h1>Wylogowywanie...</h1>
        </div>
    );
}

export default Logout;
