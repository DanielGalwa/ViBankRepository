package pl.vibank.controller;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.vibank.model.dto.CreateAccountDTO;
import pl.vibank.service.AccountService;

import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private AccountService accountService;

    @GetMapping("/")
    public ResponseEntity<?> getUserAccounts(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getUserAccounts(authentication));
    }

    @PostMapping("/")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
        } catch (NoSuchElementException | IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
