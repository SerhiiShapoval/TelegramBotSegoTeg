package ua.shapoval.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ua.shapoval.entity.AppDocument;
import ua.shapoval.entity.AppPhoto;
import ua.shapoval.service.enums.LinkType;

public interface FileService {

    public AppDocument processDoc(Message message);
    AppPhoto processPhoto(Message message);
    String generateLink(Long id, LinkType linkType);
}
