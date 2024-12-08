// import React, { useState, useContext } from "react";
import React, { useState} from "react";
import { useNavigate } from 'react-router-dom';
import httpClient from "../httpClient";
// import { AuthContext } from '../components/AuthContext';
import LoginHeader from '../components/LoginHeader';
import Footer from '../components/Footer';
import './Login.css'
import '../App.css'
import { useSecurityContext } from '../components/SecurityContext';

function Login() {
    const { sanitizeInput } = useSecurityContext(); 
    const [pid, setPid] = useState(null);
    const [password, setPassword] = useState(null);
    // const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const logIn = async () => {
      
        try {
              await httpClient.post("//localhost:8080/auth/login", {
              pid,
              password,
            });
            navigate("/twofactoryauth");
          } catch (error) {
            console.log(error.response);
            if (error.response && error.response.status === 403) {
              alert(error.response.data);
            }
          }
        };

    return (
        <div className="container-login">
            <LoginHeader />
            <hr class="divider"></hr>
            <div class="top-bar"></div>
            <hr class="divider"></hr>
            <div class="bottom-bar"></div>
            <Footer />
            <div class="button-container">
            <form>
                <div className="inputs">
                  <div>
                      <input className="input-login" placeholder="Identyfikator" type="text" value={pid} onChange={(e) => setPid(sanitizeInput(e.target.value))} id="pid"></input>
                  </div>
                  <div>
                      <input className="input-login" placeholder="Hasło" type="password" value={password} onChange={(e) => setPassword(sanitizeInput(e.target.value))} id="pass"></input>
                  </div>
                </div>
            </form>
            <button className="button-login" type='button' onClick={() => logIn()}>Zaloguj</button>
            </div>
        </div>
    )
}

export default Login;