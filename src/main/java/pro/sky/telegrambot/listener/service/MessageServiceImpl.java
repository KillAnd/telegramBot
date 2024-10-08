package pro.sky.telegrambot.listener.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageServiceImpl implements MessageService {

    private final TelegramBot telegramBot;
    Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    public MessageServiceImpl(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void send(long id, String message) {
        SendMessage sendMessage = new SendMessage(id, message);
        SendResponse execute = telegramBot.execute(sendMessage);

        if (execute.isOk()) {
            logger.info("Message for user {} with text {} has been successfully sent", id, message);
        } else {
            logger.info("Something goes wrong with sending message for user {} with text {}", id, message);
        }
    }
}
