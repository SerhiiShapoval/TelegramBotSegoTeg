package ua.shapoval.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.shapoval.entity.UpdateData;
import ua.shapoval.repository.UpdateDataRepository;
import ua.shapoval.service.MainService;

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
  // private final UpdateData data;
    private final UpdateDataRepository repository;

    @Override
    @Transactional
    public void processTextMessage(Update update) {
        repository.save(UpdateData.builder()
                        .event(update)
                         .build());

    }
}
