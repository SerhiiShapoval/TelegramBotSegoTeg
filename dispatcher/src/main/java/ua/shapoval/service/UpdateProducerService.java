package ua.shapoval.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducerService {
    void produce (String rabbitQueue, Update update);
}
