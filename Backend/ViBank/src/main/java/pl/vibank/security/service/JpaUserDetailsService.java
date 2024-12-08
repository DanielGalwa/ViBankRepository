package pl.vibank.security.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.vibank.model.exception.UniqueValueGenerationException;
import pl.vibank.security.model.dto.CreateUserDTO;
import pl.vibank.model.entity.User;
import pl.vibank.security.model.enums.Role;
import pl.vibank.model.repository.UserRepository;
import pl.vibank.security.model.entity.CustomUserDetails;
import java.security.SecureRandom;
import java.util.Optional;

@AllArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final SecureRandom secureRandom;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomUserDetails loadUserByUsername(String pid) {
        Optional<User> user = userRepository.findByPid(pid);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("Błędny identyfikator lub hasło");
        }else{
            if(user.get().getTries() >= 3){
                throw new BadCredentialsException("Konto zablokowane");
            }
        }
        return new CustomUserDetails(user.get());
    }

    public Role getUserRole(String pid) {
        User user = loadUserByUsername(pid).getUser();
        return user.getRole();
    }

    @Transactional
    public String create(CreateUserDTO request) {

        try {
            Role role = Role.valueOf(request.getRole());

            String pid = generatePid();

            String encodedPassword = passwordEncoder.encode(request.getPassword());

            User newUser = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .pid(pid)
                    .password(encodedPassword)
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .role(role)
                    .build();
            userRepository.save(newUser);

            return "Stworzono użytkownika o numerze pid:" + newUser.getPid();

        } catch (UniqueValueGenerationException e) {
            throw new UniqueValueGenerationException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Wprowadzono niepoprawne dane użytkownika:", e);
        }
    }

    private String generatePid() {
        String pid;
        int attempts = 0;
        do{
            attempts++;
            if(attempts > 100){
                throw new UniqueValueGenerationException("Nie udało się stworzyć unikalnego numeru pid po " + (attempts - 1) + " próbach");
            }

            pid = String.format("%08d", secureRandom.nextInt(100_000_000));

        }while(userRepository.findByPid(pid).isPresent());

        return pid;
    }

    @Transactional
    public void increaseTries(String pid) {
        User user = loadUserByUsername(pid).getUser();
        int tries = user.getTries();
        user.setTries(tries + 1);
        userRepository.save(user);
    }

    @Transactional
    public void restartTries(String pid) {
        Optional<User> userOptional = userRepository.findByPid(pid);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setTries(0);
            userRepository.save(user);
        }
    }
}
