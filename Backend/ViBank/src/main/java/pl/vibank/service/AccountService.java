package pl.vibank.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.vibank.model.dto.AccountDTO;
import pl.vibank.model.dto.CreateAccountDTO;
import pl.vibank.model.entity.Account;
import pl.vibank.model.entity.Transaction;
import pl.vibank.model.entity.User;
import pl.vibank.model.exception.UniqueValueGenerationException;
import pl.vibank.model.repository.AccountRepository;
import pl.vibank.security.service.JpaUserDetailsService;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountService {

    private static final String BANK_UNIT_NUMBER = "1253123456";
    private AccountRepository accountRepository;
    private AuthObjectsService authObjectsService;
    private JpaUserDetailsService jpaUserDetailsService;
    private final SecureRandom secureRandom;

    public boolean isAccountOwner(Authentication authentication, String senderAccountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(senderAccountNumber);
        List<Account> userAccounts =
                accountRepository.findByUserId(authObjectsService.getUserIdFormAuthentication(authentication));
        if(account.isEmpty()) {
            return false;
        }
        if(userAccounts.isEmpty()) {
            return false;
        }
        for(Account a : userAccounts){
            if(a.getAccountNumber().equals(account.get().getAccountNumber())) {
                return true;
            }
        }
        return false;
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw new NoSuchElementException("Nieznaleziono podanego numeru rachunku");
        }else{
            return account.get();
        }
    }

    public List<AccountDTO> getUserAccounts(Authentication authentication) {
        List<Account> userAccounts =
                accountRepository.findByUserId(authObjectsService.getUserIdFormAuthentication(authentication));
        return userAccounts.stream()
                .map(account -> AccountDTO.builder()
                        .accountNumber(account.getAccountNumber())
                        .balance(account.getBalance().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void makeTransaction(Transaction transaction) {
        Account senderAccount = transaction.getSenderAccount();
        Account recipientAccount = transaction.getRecipientAccount();
        BigDecimal senderBalance = senderAccount.getBalance();
        BigDecimal recipientBalance = recipientAccount.getBalance();

        if(senderBalance.compareTo(transaction.getAmount()) < 0){
            throw new IllegalArgumentException("Niewystarczająco środków na koncie");
        }else{
            senderAccount.setBalance(senderBalance.subtract(transaction.getAmount()));
            recipientAccount.setBalance(recipientBalance.add(transaction.getAmount()));
        }
    }

    @Transactional
    public String create(CreateAccountDTO request) {
        User user = jpaUserDetailsService.loadUserByUsername(request.getPid()).getUser();
        try {


            String accountNumber = generateAccountNumber();

            Account account = Account.builder()
                    .accountNumber(accountNumber)
                    .balance(new BigDecimal("0"))
                    .user(user)
                    .build();

            accountRepository.save(account);
            return "Stworzono nowy rachunek o numerze: " + account.getAccountNumber();
        }catch (UniqueValueGenerationException e){
            throw new UniqueValueGenerationException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Wprowadzono niepoprawne dane rachunku", e);
        }

    }

    private String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();
        String userAccountNumber;
        int attempts = 0;
        do{
            accountNumber.setLength(0);
            accountNumber.append(BANK_UNIT_NUMBER);
            attempts++;
            if(attempts > 100){
                throw new UniqueValueGenerationException("Nie udało się stworzyć unikalnego numeru rachunku po " + (attempts - 1) + " próbach");
            }

            userAccountNumber = String.format("%016d", secureRandom.nextInt(100_000_000));
            accountNumber.append(userAccountNumber);

        }while(accountRepository.findByAccountNumber(accountNumber.toString()).isPresent());
        return accountNumber.toString();
    }
}
