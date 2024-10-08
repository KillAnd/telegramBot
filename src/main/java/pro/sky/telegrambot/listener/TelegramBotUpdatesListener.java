package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.listener.service.MessageService;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final Pattern NOTIFICATION_MESSAGE_FORMAT = Pattern
            .compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private final DateTimeFormatter NOTIFICATION_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm");

    private final String WELCOME = "Привет, тебе тут рады!";

    @Autowired
    private TelegramBot telegramBot;

    private final MessageService messageService;

    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, MessageService messageService, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.messageService = messageService;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String text = update.message().text();
            Long id = update.message().chat().id();

            if ("/start".equals(text)) {
                messageService.send(id,WELCOME);
            } else {
                Matcher messageMatcher = NOTIFICATION_MESSAGE_FORMAT.matcher(text);

                if (messageMatcher.matches()) {
                    NotificationTask notificationTask = new NotificationTask();
                    notificationTask.setChatId(id);
                    notificationTask.setMessage(messageMatcher.group(3));
                    notificationTask.setNotificationDateTime(LocalDateTime.parse(
                            messageMatcher.group(1),
                            NOTIFICATION_DATE_TIME_FORMATTER
                    ));
                    notificationTaskRepository.save(notificationTask);

                    logger.info("{} has been successfully saved", notificationTask);
                } else {
                    logger.info("Unkown message format: {}", text);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
