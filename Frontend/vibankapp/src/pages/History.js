import React, { useState, useEffect, useRef, useCallback } from 'react'; 
import httpClient from "../httpClient";
import { useLocation } from 'react-router-dom';
import './Dashboard.css'; 
import './History.css'
import Footer from '../components/Footer';
import HeaderLogged from '../components/HeaderLogged';
import { useSecurityContext } from '../components/SecurityContext';

function History() {
    const { sanitizeInput } = useSecurityContext(); 
    const [transactionHistory, setTransactionHistory] = useState({});
    const [page, setPage] = useState(0);
    const location = useLocation(); 
    const { accountNumber } = location.state || {};
    const elementRef = useRef(null);
    const [hasMore, setHasMore] = useState(true);
    
    const loadNewTransactions = useCallback(async () => {
        if (!hasMore) return; 

        try {
            const response = await httpClient.get(
                `http://localhost:8080/transactions?accountNumber=${encodeURIComponent(accountNumber)}&page=${encodeURIComponent(page)}&size=10`
            );

            const newTransactions = response.data; //od API: Map<LocalDate, List<ShowTransactionDTO>>

            if (Object.keys(newTransactions).length === 0) {
                setHasMore(false); 
            } else {
                setTransactionHistory(prevHistory => {
                    const updatedHistory = { ...prevHistory };

                    // Aktualizujemy historię transakcji
                    Object.entries(newTransactions).forEach(([date, transactions]) => {
                        if (updatedHistory[date]) {
                            updatedHistory[date] = [...updatedHistory[date], ...transactions];
                        } else {
                            updatedHistory[date] = transactions;
                        }
                    });

                    // Sortowanie historii transakcji po dacie malejąco
                    const sortedHistory = Object.entries(updatedHistory).sort((a, b) => new Date(b[0]) - new Date(a[0]));

                    // Konwersja sortedHistory z powrotem do obiektu
                    return Object.fromEntries(sortedHistory);
                });

                setPage(prevPage => prevPage + 1); 
            }
        } catch (error) {
            return <h1>Wystąpił błąd: {sanitizeInput(error)}</h1>;
        }
    }, [accountNumber, page, hasMore,sanitizeInput]); 

    const onIntersection = useCallback((entries) => {
        const firstEntry = entries[0];
        if (firstEntry.isIntersecting && hasMore) {
            loadNewTransactions();
        }
    }, [hasMore, loadNewTransactions]); 

    useEffect(() => {
        const observer = new IntersectionObserver(onIntersection);
        if (observer && elementRef.current) {
            observer.observe(elementRef.current);
        }
        
        return () => {
            if (observer) {
                observer.disconnect();
            }
        };
    }, [onIntersection]); 

    return (
        <div className="container-dashboard">
            <HeaderLogged />
            <hr class="divider"></hr>
            <div className="main-content-history">
                <div className='transaction-container'>
                    <h3 className='padding-header'>{`Operacje na koncie: ${sanitizeInput(accountNumber)}`}</h3>
                    {Object.keys(transactionHistory).length > 0 ? (
                        <div className="transaction-container-history">
                            {Object.entries(transactionHistory).map(([date, transactions], dateIndex) => (
                                <div key={date}>
                                    <h4>{new Date(date).toLocaleDateString('pl-PL')}</h4>
                                    {transactions.map((transaction, transactionIndex) => (
                                        <div key={sanitizeInput(transactionIndex)} className='transaction'>
                                            <div className="transaction-info">
                                                {transaction.profit ? (
                                                    <span className="icon green-arrow">&#x2192;</span>
                                                ) : (
                                                    <span className="icon red-arrow">&#x2190;</span>
                                                )}
                                                <p>{sanitizeInput(transaction.title)}</p>
                                            </div>
                                            <div className="transaction-amount">
                                                <p>{transaction.profit ? `${sanitizeInput(transaction.amount)}` : `-${sanitizeInput(transaction.amount)}`} PLN</p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div>Nie znaleziono transakcji</div>
                    )}
                    {hasMore &&
                        <div ref={elementRef} style={{textAlign: 'center'}}>  Loading... </div>}
                </div>        
            </div> 
            <hr class="divider"></hr>          
            <Footer />
        </div>
    );
}

export default History;
