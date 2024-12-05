package pl.vibank.security.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import pl.vibank.model.exception.UniqueValueGenerationException;
import pl.vibank.security.model.dto.CreateUserDTO;
import pl.vibank.security.model.dto.RestartTriesDTO;
import pl.vibank.security.service.AuthService;
import pl.vibank.security.model.dto.LoginDTO;
import pl.vibank.security.service.JpaUserDetailsService;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JpaUserDetailsService jpaUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO,
                                   HttpServletRequest request, HttpServletResponse response) {

        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.authenticate(loginDTO, request, response));
        }catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.logout(response));
        }catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody CreateUserDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(jpaUserDetailsService.create(request));
        } catch (UniqueValueGenerationException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/restart-tries")
    public ResponseEntity<?> restartTries(@RequestBody RestartTriesDTO request) {
        try {
            jpaUserDetailsService.restartTries(request.getPid());
            return ResponseEntity.status(HttpStatus.OK).body("Licznik został zresetowany");
        }catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
