import React, { createContext, useContext } from 'react';


const SecurityContext = createContext();

export const useSecurityContext = () => useContext(SecurityContext);

export function SecurityProvider({ children }) {
    function getCookie(cookieName) {
        const cookies = document.cookie.split("; ");
        for (let cookie of cookies) {
            const [name, value] = cookie.split("=");
            if (name === cookieName) {
                return decodeURIComponent(value);
            }
        }
        return null;
    }

    function sanitizeInput(input) {
        if (typeof input !== 'string') {
            input = String(input);
        }
        
        return input
        .replace(/&/g, '') // Usuwanie & 
        .replace(/</g, '')  // Usuwanie < 
        .replace(/>/g, '')  // Usuwanie >
        .replace(/"/g, '') // Usuwanie "
        .replace(/'/g, '') // Usuwanie ' 
        .replace(/\//g, '') // Usuwanie /
        .replace(/`/g, ''); // Usuwanie `
    }

    return (
        <SecurityContext.Provider value={{  getCookie, sanitizeInput }}>
            {children}
        </SecurityContext.Provider>
    );
}