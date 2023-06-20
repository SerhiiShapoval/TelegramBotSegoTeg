package ua.shapoval.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.shapoval.entity.AppUser;
import ua.shapoval.entity.UpdateData;
import ua.shapoval.entity.UserState;
import ua.shapoval.repository.AppUserRepository;
import ua.shapoval.repository.UpdateDataRepository;
import ua.shapoval.service.MainService;
import ua.shapoval.service.ProducerService;

import javax.transaction.Transactional;

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

    @Override
    public void processTextMessage(Update update) {
     saveData(update);
     String output = "";
      var appUser = findOrSaveAppUser(update);
      var userState = appUser.getUserState();
      var text = update.getMessage().getText();
      if (CANCEL.equals(text)){
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
        var answer = " Photo uploaded successfully. Download link : ";
        sendAnswer(answer, chatId);

    }

    @Override
    public void processDocMessage(Update update) {
        saveData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        var answer = " Document uploaded successfully. Download link : ";
        sendAnswer(answer, chatId);
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
