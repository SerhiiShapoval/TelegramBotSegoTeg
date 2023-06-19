package ua.shapoval.service.serviceImpl;

import lombok.RequiredArgsConstructor;
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

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
  // private final UpdateData data;
    private final UpdateDataRepository repository;
    private final AppUserRepository userRepository;

    @Override
    @Transactional
    public void processTextMessage(Update update) {
     var appUser = findOrSaveAppUser(update.getMessage().getFrom());
        repository.save(UpdateData.builder()
                        .event(update)
                         .build());

    }


    private AppUser findOrSaveAppUser(User userTelegram){
      var persistentAppUser = userRepository.findAppUserByTelegramUserId(userTelegram.getId());
      if (persistentAppUser == null){
        return userRepository.save(AppUser.builder()
                        .telegramUserId(userTelegram.getId())
                        .userName(userTelegram.getUserName())
                        .firstName(userTelegram.getFirstName())
                        .lastName(userTelegram.getLastName())
                        .isActive(true)
                        .userState(UserState.BASIC_STATE)
                .build());
      }
      return userRepository.save(persistentAppUser);
    }
}
