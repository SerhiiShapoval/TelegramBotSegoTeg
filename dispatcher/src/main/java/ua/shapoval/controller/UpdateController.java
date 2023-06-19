package ua.shapoval.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.shapoval.service.UpdateProducerService;
import ua.shapoval.model.RabbitQueue;
import ua.shapoval.utils.MassageUtils;

@Component
@Log4j
@RequiredArgsConstructor
public class UpdateController {

    private final MassageUtils massageUtils;
    private final UpdateProducerService updateProducerService;
    private  TelegramBot telegramBot;

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if (update == null){
            log.error("Received update is null ");
            return;
        }
        if (update.hasMessage()){
            distributeMassageByType(update);
        }else{
            log.error("Received unsupported massage type " + update);
        }
    }

    private void distributeMassageByType(Update update) {
        var massage = update.getMessage();
        if (massage.hasText()  ){
            processTextMessage(update);
        }else if (massage.hasDocument()){
            processDocMessage(update);
        }else if (massage.hasPhoto()){
            processPhotoMassage(update);
        }else {
            setUnsupportedMessageTypeView(update);
        }


    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = massageUtils.generateSendMessageWithText(update,
                "Unsupported Message Type !");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);

    }

    private void processPhotoMassage(Update update) {
        updateProducerService.produce(RabbitQueue.PHOTO_MESSAGE_UPDATE,update);
        setFileReceivedView(update);
    }

    private void processDocMessage(Update update) {
        updateProducerService.produce(RabbitQueue.DOC_MESSAGE_UPDATE,update);
        setFileReceivedView(update);
    }

    private void processTextMessage(Update update) {
        updateProducerService.produce(RabbitQueue.TEXT_MESSAGE_UPDATE,update);
    }

    private void setFileReceivedView(Update update) {
        var sendMessage = massageUtils.generateSendMessageWithText(update,
                "File is being processed, please waite !");
        setView(sendMessage);
    }


}
