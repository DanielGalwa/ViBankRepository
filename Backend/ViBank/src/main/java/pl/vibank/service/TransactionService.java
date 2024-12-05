package pl.vibank.service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.vibank.model.dto.CreateTransactionDTO;
import pl.vibank.model.dto.ShowTransactionDTO;
import pl.vibank.model.entity.Account;
import pl.vibank.model.entity.Transaction;
import pl.vibank.model.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;



@Service
@AllArgsConstructor
public class TransactionService {

    private AccountService accountService;
    private TransactionRepository transactionRepository;

    @Transactional
    public String makeTransaction(CreateTransactionDTO createTransactionDTO, Authentication authentication) {

        if (!accountService.isAccountOwner(authentication, createTransactionDTO.getSenderAccountNumber())) {
            throw new SecurityException("Nie jesteś właścicielem tego rachunku");
        }

        BigDecimal bigValue = new BigDecimal(createTransactionDTO.getAmount());
        Transaction transaction = Transaction.builder()
                .transactionRecipientName(createTransactionDTO.getTransactionRecipientName())
                .title(createTransactionDTO.getTitle())
                .amount(bigValue)
                .date(LocalDateTime.now())
                .senderAccount(accountService.getAccountByAccountNumber(createTransactionDTO.getSenderAccountNumber()))
                .recipientAccount(accountService.getAccountByAccountNumber(createTransactionDTO.getRecipientsAccountNumber()))
                .build();

        accountService.makeTransaction(transaction);
        transactionRepository.save(transaction);

        return "Transakcje przeprowadzono pomyślnie";
    }

    public Map<LocalDate, List<ShowTransactionDTO>> getTransactions(String accountNumber, String page, String size, Authentication authentication) {

        try {
            int pageInt = Integer.parseInt(page);
            int pageSize = Integer.parseInt(size);

            if(pageSize > 20){
                throw new IllegalArgumentException("Rozmiar strony nie może być większy niż 20 transakcji");
            }

            Pageable pageable = PageRequest.of(pageInt, pageSize, Sort.by("date").descending());

        if (!accountService.isAccountOwner(authentication, accountNumber)) {
            throw new SecurityException("Nie jesteś właścicielem tego rachunku");
        }
        Account account = accountService.getAccountByAccountNumber(accountNumber);

        return transactionRepository.findBySenderAccountOrRecipientAccount(account, account, pageable).stream()
                .map(transaction -> ShowTransactionDTO.builder()
                        .transactionRecipientName(transaction.getTransactionRecipientName())
                        .title(transaction.getTitle())
                        .recipientsAccountNumber(transaction.getRecipientAccount().getAccountNumber())
                        .amount(transaction.getAmount().toString())
                        .senderAccountNumber(transaction.getSenderAccount().getAccountNumber())
                        .isProfit(transaction.getRecipientAccount().getAccountNumber().equals(accountNumber))
                        .date(transaction.getDate().toLocalDate())
                        .build())
                .collect(Collectors.groupingBy(
                        ShowTransactionDTO::getDate,
                        TreeMap::new,
                        Collectors.toList()
                ));
        }catch(NumberFormatException e) {
            throw new NumberFormatException("Numer strony oraz jej rozmaiar musi być liczbą całkowitą ");
        }
    }
}

