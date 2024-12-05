package pl.vibank.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.vibank.model.entity.User;
import pl.vibank.security.model.entity.CustomUserDetails;

@Service
public class AuthObjectsService {

    public int getUserIdFormAuthentication(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getUser();
        return user.getId();
    }
}
