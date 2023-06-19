package ua.shapoval.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumerService {
    void consumer(SendMessage sendMessage);
}
