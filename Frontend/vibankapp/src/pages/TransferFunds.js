import React, { useState, useEffect } from 'react'; 
import httpClient from "../httpClient";
import { useNavigate, useLocation } from 'react-router-dom'; 
import HeaderLogged from '../components/HeaderLogged';
import Footer from '../components/Footer';
import './TransferFunds.css';
import { useSecurityContext } from '../components/SecurityContext';


function TransferFunds() {

    const { getCookie } = useSecurityContext();
    const location = useLocation(); 

    const [formData, setFormData] = useState({
        transactionRecipientName: '',
        title: '',
        recipientsAccountNumber: '',
        amount: '',
        senderAccountNumber: ''
    });

    const { accountNumber } = location.state || {};

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (accountNumber) {
            setFormData((prevFormData) => ({
                ...prevFormData,
                senderAccountNumber: accountNumber
            }));
        }
    }, [accountNumber]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    
    const handleSubmit = async (event) => {
        event.preventDefault();
        setLoading(true);
        setError(null);
        setSuccessMessage(null);

        try {
            const csrfToken = getCookie("CSRFcookie")
            const response = await httpClient.post('http://localhost:8080/transactions/', formData ,{
                headers: {
                    'X-CSRF-Token': csrfToken
                }
            });
            setSuccessMessage(response.data);
            navigate('/dashboard');
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <h1>Ładowanie...</h1>;
    }

    if (error) {
        return <h1>Wystąpił błąd: {error}</h1>;
    }
    
    return (
        <div className='transfer-page'>
            <HeaderLogged />
            <hr className="divider" />
            <div className='main-content-transaction'>
                <div className='transferContainer'>
                    <h1 className='padding-header-transaction'>Zleć przelew</h1>
                    {!successMessage ? (
                    <form onSubmit={handleSubmit}>
                        <div className="input-group">
                            <label>Nazwa odbiorcy:</label>
                            <input
                                className="input-transfer"
                                type="text"
                                name="transactionRecipientName"
                                value={formData.transactionRecipientName}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Tytuł przelewu:</label>
                            <input
                                className="input-transfer"
                                type="text"
                                name="title"
                                value={formData.title}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Numer konta odbiorcy:</label>
                            <input
                                className="input-transfer"
                                type="text"
                                name="recipientsAccountNumber"
                                value={formData.recipientsAccountNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label>Kwota:</label>
                            <input
                                className="input-transfer"
                                type="text"
                                name="amount"
                                value={formData.amount}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <button className="btn-primary" type="submit" disabled={loading}>
                            {loading ? 'Wysyłanie...' : 'Wyślij przelew'}
                        </button>
                        
                    </form>
                    ) : (
                        <div>
                            {successMessage.data}
                        </div>
                    )}
                </div>
            </div>
            <hr className="divider" />
            <Footer />
        </div>
    );
}

export default TransferFunds;
