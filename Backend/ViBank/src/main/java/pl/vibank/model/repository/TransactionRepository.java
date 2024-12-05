package pl.vibank.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.vibank.model.entity.Account;
import pl.vibank.model.entity.Transaction;



@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Page<Transaction> findBySenderAccountOrRecipientAccount(Account senderAccount, Account recipientAccount, Pageable pageable);
}
