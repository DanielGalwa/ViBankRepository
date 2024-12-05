import React, { useState} from "react";
import { useNavigate } from 'react-router-dom';
import httpClient from "../httpClient";
import { useSecurityContext } from '../components/SecurityContext';
import LoginHeader from '../components/LoginHeader';
import Footer from '../components/Footer';
import './Login.css'
import '../App.css'
import './TwoFactoryAuth.css'

function TwoFactoryAuth(){
    const { sanitizeInput } = useSecurityContext(); 
    const [codeValue, setCodeValue] = useState("");
    const navigate = useNavigate();

    const twoFactor = async () => {
        try {
            await httpClient.post("http://localhost:8080/auth/2fa", {
            codeValue,
          });
          navigate("/dashboard");
        } catch (error) {
          if (error.response.status === 401) {
            alert("Niepoprawnie uzupełniony formularz");
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
            <div class="button-container-2fa">
              <form>
                <div className="inputs-2fa">
                    <div>
                        <input className="input-login" placeholder="Kod" type="text" value={codeValue} onChange={(e) => setCodeValue(sanitizeInput(e.target.value))}></input>
                    </div>
                </div>
              </form>
              <div>
                  <button className="button-login" type='button' onClick={() => twoFactor()}>Wyslij kod</button>
              </div>
            </div>
        </div>
    )
}
export default TwoFactoryAuth