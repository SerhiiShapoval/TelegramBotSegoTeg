package ua.shapoval.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MassageUtils {
    public SendMessage generateSendMessageWithText(Update update, String text){
    var message = update.getMessage();
    var sendMessage = new SendMessage().builder()
            .chatId(message.getChatId().toString())
            .text(text)
            .build();
    return sendMessage;
    }
}
