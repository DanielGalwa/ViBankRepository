package pl.vibank.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;

@Configuration
public class BeansConfiguration {
    @Bean
    public AuthenticationManager authenticationManager
            (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
//
//    @Autowired
//    public AuthenticationConfiguration authenticationConfiguration;
//

//    @Bean
//public AuthenticationManager authenticationManager() throws Exception {
//    AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
//
//    if (authenticationManager instanceof ProviderManager providerManager) {
//        List<AuthenticationProvider> providers = providerManager.getProviders();
//        System.out.println("=== Zarejestrowane Authentication Providers ===");
//        providers.forEach(provider -> System.out.println(provider.getClass().getName()));
//    } else {
//        System.out.println("AuthenticationManager nie jest instancją ProviderManager.");
//    }
//
//    return authenticationManager;
//}