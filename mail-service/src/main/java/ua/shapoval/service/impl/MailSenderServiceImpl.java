package ua.shapoval.service.impl;


import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.shapoval.dto.MailCredential;
import ua.shapoval.service.MailSenderService;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationUri;

    @Override
    public void sendMail(MailCredential mailCredential) {
        
        var subject = "Activation email address";
        var messageBody = getActivationMailBody(mailCredential.getId());
        var emailTo = mailCredential.getEmailTo();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailFrom);
        simpleMailMessage.setTo(emailTo);
        simpleMailMessage.setSubject(subject);
        javaMailSender.send(simpleMailMessage);
    }

    private String getActivationMailBody(String id) {
        var message = String.format("To complete the registration follow the links:\n%s", activationUri);
        return message.replace("{id}", id);
    }
}
