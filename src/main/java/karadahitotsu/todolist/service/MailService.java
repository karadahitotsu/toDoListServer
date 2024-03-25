package karadahitotsu.todolist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_google_account_email");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    public void sendToken(String to,long id,String token){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_google_account_email");
        message.setTo((to));
        message.setSubject("Verification");
        String link = "http://localhost:8080/api/verification?user_id=" + id + "&token=" + token;
        message.setText("Для верификации перейдите по ссылке - "+link);
        mailSender.send(message);
    }
}
