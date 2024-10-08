package pro.sky.telegrambot.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.listener.service.MessageService;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationJob {

    private Logger logger = LoggerFactory.getLogger(NotificationJob.class);

    private NotificationTaskRepository notificationTaskRepository;
    private final MessageService messageService;

    public NotificationJob(NotificationTaskRepository notificationTaskRepository, MessageService messageService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.messageService = messageService;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void notifications() {
        LocalDateTime currentDataTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        logger.info("Job started date time {}", currentDataTime);

        List<NotificationTask> notificationTasks = notificationTaskRepository.findAllByNotificationDateTime(currentDataTime);

        logger.info("{} tasks have been found", notificationTasks.size());

        for (NotificationTask notificationTask : notificationTasks) {
            messageService.send(notificationTask.getChatId(),
                    "Напоминалочка! " + notificationTask.getMessage());
        logger.info("Notification with id {} has been successfully sent", notificationTask.getId());
        }

        logger.info("Job finished!");

    }

}
