package ua.shapoval.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.shapoval.dto.MailCredential;
import ua.shapoval.service.MailSenderService;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailSenderService mailSenderService;

    @PostMapping
    public ResponseEntity<?> sendActivationMail(@RequestBody MailCredential mailCredential){

        mailSenderService.sendMail(mailCredential);

        return ResponseEntity.ok().build();
    }
}
