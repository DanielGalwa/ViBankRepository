package pl.vibank.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.vibank.model.dto.CreateTransactionDTO;
import pl.vibank.service.TransactionService;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping()
    public ResponseEntity<?> getUserTransactions(@RequestParam("accountNumber") String accountNumber,
                                                 @RequestParam(defaultValue = "0") String page,
                                                 @RequestParam(defaultValue = "6") String size,
                                                 Authentication authentication) {

        try{
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactions(accountNumber, page, size,authentication));
        }catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> makeTransaction(@RequestBody CreateTransactionDTO createTransactionDTO, Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.makeTransaction(createTransactionDTO, authentication));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
