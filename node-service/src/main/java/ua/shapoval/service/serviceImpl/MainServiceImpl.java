package ua.shapoval.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.shapoval.entity.*;
import ua.shapoval.exeptions.UploadFileException;
import ua.shapoval.repository.AppUserRepository;
import ua.shapoval.repository.UpdateDataRepository;
import ua.shapoval.service.FileService;
import ua.shapoval.service.MainService;
import ua.shapoval.service.ProducerService;
import ua.shapoval.service.enums.LinkType;
import ua.shapoval.service.enums.ServiceCommands;

import static ua.shapoval.entity.UserState.BASIC_STATE;
import static ua.shapoval.entity.UserState.WAIT_FOR_EMAIL_STATE;
import static ua.shapoval.service.enums.ServiceCommands.*;


@Service
@RequiredArgsConstructor
@Log4j
public class MainServiceImpl implements MainService {
  // private final UpdateData data;
    private final UpdateDataRepository repository;
    private final AppUserRepository userRepository;
    private final ProducerService producerService;
    private final FileService fileService;


    @Override
    public void processTextMessage(Update update) {
     saveData(update);
     String output = "";
      var appUser = findOrSaveAppUser(update);
      var userState = appUser.getUserState();
      var text = update.getMessage().getText();

        var serviceCommand = ServiceCommands.fromValues(text);
      if (CANCEL.equals(serviceCommand)){
        output = cancelProcess(appUser);
      }else if (BASIC_STATE.equals(userState)){
        output = processServiceCommand(appUser, text);
      }else  if (WAIT_FOR_EMAIL_STATE.equals(userState)){
        //todo
      }else {
        log.error(" Unknown user state : " + userState);
        output = " Unknown error! Press /cancel and try again ";
      }
      sendAnswer(output, update.getMessage().getChatId());

    }

    private void saveData(Update update){
        repository.save(UpdateData.builder()
                .event(update)
                .build());
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = " Photo uploaded successfully." +
                    " Download link : " + link;
            sendAnswer(answer, chatId);
        }catch (UploadFileException exception){
            log.error(exception);
            var error = "Sorry, the photo could not be loaded. Please try again later";
            sendAnswer(error, chatId);

        }


    }

    @Override
    public void processDocMessage(Update update) {
        saveData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            var answer = " Document uploaded successfully. Download link : " + link;
            sendAnswer(answer, chatId);
        }catch (UploadFileException exception){
            log.error(exception);
            var error = "Sorry, the file could not be loaded. Please try again later";
            sendAnswer(error, chatId);
        }

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {

        var userState = appUser.getUserState();

        if (!appUser.isActive()){
            var error = " Register or activate your account";
            sendAnswer(error,chatId);
            return true;
        }else if (!BASIC_STATE.equals(userState)){
            var error = "Press /cancel for canceled command";
            sendAnswer(error,chatId);
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);

  }

  private String processServiceCommand(AppUser appUser, String cmd) {
    if (REGISTRATION.equals(cmd)) {
      //todo
      return "Time out";
    }else if (HELP.equals(cmd)){
      return help();
    }else if (START.equals(cmd)){
      return " Hello! Chose command /help";
    }else {
      return "Unknown command, press /help ";
    }

  }

  private String help() {
      return "Press command:\n"
              +"/cancel - cancel command execution\n"
              +"/registration - registration user\n";
  }

  private String cancelProcess(AppUser appUser) {
      appUser.setUserState(BASIC_STATE);
      userRepository.save(appUser);
      return "Command canceled";

  }


  private AppUser findOrSaveAppUser(Update update){
      var userTelegram = update.getMessage().getFrom();
      var persistentAppUser = userRepository.findAppUserByTelegramUserId(userTelegram.getId());
      if (persistentAppUser == null){
        return userRepository.save(AppUser.builder()
                        .telegramUserId(userTelegram.getId())
                        .userName(userTelegram.getUserName())
                        .firstName(userTelegram.getFirstName())
                        .lastName(userTelegram.getLastName())
                        .isActive(true)
                        .userState(BASIC_STATE)
                .build());
      }
      return userRepository.save(persistentAppUser);
    }
}
