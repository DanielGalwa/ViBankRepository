import React, { useState, useEffect } from 'react';
import httpClient from "../httpClient";
import HeaderLogged from '../components/HeaderLogged';
import Footer from '../components/Footer';
import './Dashboard.css'; 
import '../App.css';
import { Link } from 'react-router-dom';
import { useSecurityContext } from '../components/SecurityContext';

function Dashboard() {
    const { sanitizeInput } = useSecurityContext(); 
    const [accounts, setAccounts] = useState(null);
    const [transactionHistories, setTransactionHistories] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [accountNumberString, setAccountNumberString] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const accountResponse = await httpClient.get('http://localhost:8080/accounts/');
                const accountData = accountResponse.data;
                setAccounts(accountData);

                if (accountData && accountData.length > 0) {
                    setAccountNumberString(accountData[0].accountNumber);
                }
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchData(); 
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            if (!accountNumberString) return;

            try {
                const transactionHistoriesResponse = await httpClient.get(`http://localhost:8080/transactions?accountNumber=${encodeURIComponent(accountNumberString)}&page=0&size=6`);
                const sortedTransactions = Object.entries(transactionHistoriesResponse.data).sort((a, b) => new Date(b[0]) - new Date(a[0]));
                setTransactionHistories(sortedTransactions);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [accountNumberString]);

    const handleAccountChange = (accountNumber) => {
        setAccountNumberString(accountNumber);
    };

    if (loading) {
        return <h1>Ładowanie...</h1>;
    }

    if (error) {
        return <h1>Wystąpił błąd: {sanitizeInput(error)}</h1>;
    }

    return (
        <div className="container-dashboard">
            <HeaderLogged />
            <hr className="divider"></hr>
            <div className="main-content-dashboard">
                <div className="left-side">
                    {accounts && accounts.length > 0 ? (
                        <div>
                            {accounts.map(account => (
                                <div key={account.accountNumber} className="card">
                                    <div className="card-info">
                                        <h2>Konto krajowe</h2>
                                        <p>{sanitizeInput(account.accountNumber)}</p>
                                    </div>
                                    <div className="account-balance">
                                        <p>Dostępne środki</p>
                                        <h3>{sanitizeInput(account.balance)} PLN</h3>
                                        <div className="button-group">
                                            <button 
                                                className="btn-outline-primary"  
                                                onClick={() => handleAccountChange(account.accountNumber)}
                                            >
                                                Wyświetl transakcje
                                            </button>
                                            <Link 
                                                to="/transferFunds"
                                                state={{ accountNumber: account.accountNumber }}
                                            >
                                                <button className="btn-primary">Przelew</button>
                                            </Link>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div>
                            <div className="card">
                                <p>Brak dostępnych kont.</p>
                            </div>
                        </div>
                    )}
                </div>
                <div className="right-side">
                    <div className="history-container">
                        <h3 className="padding-header">{`Operacje na koncie: ${sanitizeInput(accountNumberString)}`}</h3>
                        <Link 
                            to="/history" 
                            state={{ accountNumber: accountNumberString }} 
                            className="history-link"
                        >
                            CAŁA HISTORIA
                        </Link>
                        {transactionHistories && Object.keys(transactionHistories).length > 0 ? (
                            <div>
                                {transactionHistories.map(([date, transactions]) => (
                                    <div key={sanitizeInput(date)}>
                                        <h4>{new Date(date).toLocaleDateString('pl-PL')}</h4>
                                        {transactions.map((history, index) => (
                                            <div key={index} className="transaction">
                                                <div className="transaction-info">
                                                    {history.profit ? (
                                                        <span className="icon green-arrow">&#x2192;</span>
                                                    ) : (
                                                        <span className="icon red-arrow">&#x2190;</span>
                                                    )}
                                                    <p>{sanitizeInput(history.title)}</p>
                                                </div>
                                                <div className="transaction-amount">
                                                    <p>{history.profit ? `${sanitizeInput(history.amount)}` : `-${sanitizeInput(history.amount)}`} PLN</p>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="history">
                                <p>Brak transakcji dla wybranego konta.</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <hr className="divider"></hr>
            <Footer />
        </div>
    );
}

export default Dashboard;
