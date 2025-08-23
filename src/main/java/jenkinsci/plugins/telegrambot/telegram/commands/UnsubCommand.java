package jenkinsci.plugins.telegrambot.telegram.commands;

import jenkinsci.plugins.telegrambot.users.Subscribers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import jenkinsci.plugins.telegrambot.telegram.TelegramBot;

public class UnsubCommand extends AbstractBotCommand {

    private static final String LOG_TAG = "/unsub";
    private static final Logger LOGGER = LoggerFactory.getLogger(UnsubCommand.class);

    public UnsubCommand() {
        super("unsub", "command.unsub");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String[] arguments) {
        LOGGER.info("Executing unsub command for user: {}", user.getId());
        
        Subscribers subscribers = Subscribers.getInstance();
        String messageText;

        Long id = chat.getId();

        boolean isSubscribed = subscribers.isSubscribed(id);

        if (isSubscribed) {
            subscribers.unsubscribe(id);
            messageText = botStrings.get("message.unsub.success");
        } else {
            messageText = botStrings.get("message.unsub.alreadyunsub");
        }

        bot.sendMessage(chat.getId(), messageText);
    }
}
