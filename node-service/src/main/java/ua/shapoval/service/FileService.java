package ua.shapoval.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ua.shapoval.entity.AppDocument;

public interface FileService {

    public AppDocument processDoc(Message message);
}
