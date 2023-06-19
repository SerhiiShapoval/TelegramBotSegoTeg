package ua.shapoval.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.shapoval.controller.UpdateController;
import ua.shapoval.service.AnswerConsumerService;

import static ua.shapoval.model.RabbitQueue.ANSWER_MESSAGE;


@Service
@RequiredArgsConstructor
public class AnswerConsumerServiceImpl implements AnswerConsumerService {

    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumer(SendMessage sendMessage) {
    updateController.setView(sendMessage);
    }
}
