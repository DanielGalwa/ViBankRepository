package pl.vibank.security.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.vibank.config.MailConfig;
import pl.vibank.security.model.dto.TwoFactoryAuthDTO;
import pl.vibank.model.entity.User;
import pl.vibank.security.model.entity.Code;
import pl.vibank.security.repository.CodeRepository;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CodeService {

    private final SecureRandom secureRandom;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final CodeRepository codeRepository;

    //Server email
    //https://www.baeldung.com/java-email
    //https://www.baeldung.com/spring-email
    //https://wpmailsmtp.com/gmail-less-secure-apps/#Option_1_Switch_to_the_Gmail_Mailer
    //https://support.google.com/accounts/answer/185833?hl=pl#zippy=%2Cwci%C4%85%C5%BC-nie-mo%C5%BCna-si%C4%99-zalogowa%C4%87%2Chas%C5%82a-do-aplikacji-zosta%C5%82y-uniewa%C5%BCnione-po-zmianie-has%C5%82a
    private final JavaMailSender javaMailSender;
    private final MailConfig mailConfig;

    //authService stad wygeneruje i wysle kod
    @Transactional
    public void sendCodeToEmail(String pid) {
        User user = jpaUserDetailsService.loadUserByUsername(pid).getUser();
        String code = generateCode(user);
        System.out.println(code);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(mailConfig.getUsername());
//        message.setTo(user.getEmail());
//        message.setSubject("Kod weryfikacji do aplikacji ViBank");
//        message.setText(code);
//        javaMailSender.send(message);

    }

    //2fa service sprawdzi czy podany kod jest git
    public boolean checkIfCodeValid(TwoFactoryAuthDTO twoFactoryAuthDTO, String pid) {
        User user = jpaUserDetailsService.loadUserByUsername(pid).getUser();
        if(checkIfCodeIsNotExpired(user)){
            if(checkIfUserIsOwnerOfCode(user,twoFactoryAuthDTO.getCodeValue())){
                return true;
            }
        }
        return false;
    }

    private boolean checkIfUserIsOwnerOfCode(User user, String code) {
        Optional<Code> UserAttachedToCode = codeRepository.findByUser(user);
        if(UserAttachedToCode.isPresent()){
            if(code.equals(UserAttachedToCode.get().getCodeValue())){
                return true;
            }
        }
        return false;
    }

    private boolean checkIfCodeIsNotExpired(User user){
        Optional<Code> codeAttachedToUser = codeRepository.findByUser(user);
        if(codeAttachedToUser.isEmpty()){
            return false;
        }
        if(codeAttachedToUser.get().getExpirationDate().after(new Date())){
            return true;
        }
        return false;
    }

    private String generateCode(User user) {
        Optional<Code> userAttachedToCode = codeRepository.findByUser(user);

        Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 3);

        if(userAttachedToCode.isPresent()) {

            userAttachedToCode.get().setExpirationDate(date);
            userAttachedToCode.get().setCodeValue(generateRandomValue());
            return  userAttachedToCode.get().getCodeValue();
        }

        Code code = codeRepository.save(new Code(generateRandomValue(),user,date));
        return code.getCodeValue();
    }

    private String generateRandomValue(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++){
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}
