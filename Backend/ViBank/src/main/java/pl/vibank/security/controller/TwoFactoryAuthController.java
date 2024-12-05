package pl.vibank.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.vibank.security.model.dto.TwoFactoryAuthDTO;
import pl.vibank.security.service.TwoFactoryService;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class TwoFactoryAuthController {

    private final TwoFactoryService twoFactoryService;

    @PostMapping("/2fa")
    public ResponseEntity<?> login(@RequestBody TwoFactoryAuthDTO twoFactoryAuthDTO,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(twoFactoryService.authenticate(twoFactoryAuthDTO, request, response));
        }catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
