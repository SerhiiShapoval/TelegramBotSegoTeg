package ua.shapoval.service;


import ua.shapoval.dto.MailCredential;

public interface MailSenderService {

    void sendMail(MailCredential mailCredential);
}
