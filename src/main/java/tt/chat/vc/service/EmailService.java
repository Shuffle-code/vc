package tt.chat.vc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender sender;

    public String sendConfirmationCode(String code, String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(from);
            message.setTo(email);
            message.setSubject("Приложение: '_ViddeoStrim TT Nsk_'");
            message.setText("\nВаш код для подтверждения регистрации: \n" + code + "\nС Уважением администрация: \n" +
                    "vs-sport.online");

            sender.send(message);

            return "STATUS OK.";
        } catch (Exception e) {
            return "STATUS FAILED.";
        }
    }

    

}
