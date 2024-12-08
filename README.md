# ViBankRepository
This application is a secure online banking platform that allows users to manage their accounts and perform transactions. It provides a well-structured interface for viewing account balances, reviewing transaction histories, and processing fund transfers. Additionally, the system integrates two-factor authentication and multiple security mechanisms—such as Content Security Policies, CSRF tokens, HttpOnly cookies with the SameSite=Strict attribute, password brute-force prevention, and input sanitization—to protect user data from unauthorized access.

## Table of Contents
- [Technologies](#Technologies)
- [Setup](#Setup)
- [Global Architecture](#Global-Architecture)
- [List of Security Features](#List-of-Security-Features)
- [Design](#Design)
- [API](#API)
- [Spring Security](#Spring-Security)

# Technologies
- **Spring Boot**: 3.3.2 version
- **Spring Security**: 6.1.2 version
- **React**: 18.3.1 version
- **Java**: 17 version
- **MySQL**: 8.0.39 version

# Setup

# Global Architecture
<div align="center">
  <img src="architecture.png" alt="Architecture Diagram" />
</div>

# List of Security Features

- Two Factory Authentication
- Hashing Passwords
- CSRF:
    - Token CSRF
    - CSRF Token generated using Secure Random
    - SameSite Strict cookie attribute
- Bruteforce Password Protection
- XSS
    - Content Security Policy (CSP)
    - HttpOnly cookies
    - Input and Output Sanitization (Additional, React provides its own mechanisms)
    
# Design
<div align="center">
  <img src="design.png" alt="Design dashboard view" />
</div>

<div align="center">
  <img src="design2.png" alt="Design logging view" />
</div>
