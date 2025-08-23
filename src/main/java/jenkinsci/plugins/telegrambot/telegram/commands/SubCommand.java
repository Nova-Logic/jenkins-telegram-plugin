package jenkinsci.plugins.telegrambot.telegram.commands;

import jenkinsci.plugins.telegrambot.users.Subscribers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import jenkinsci.plugins.telegrambot.telegram.TelegramBot;

public class SubCommand extends AbstractBotCommand {

    private static final String LOG_TAG = "/sub";
    private static final Logger LOGGER = LoggerFactory.getLogger(SubCommand.class);

    public SubCommand() {
        super("sub", "command.sub");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String[] arguments) {
        LOGGER.info("Executing sub command for user: {}", user.getId());
        
        Subscribers subscribers = Subscribers.getInstance();
        String messageText;

        Long id = chat.getId();
        String name = chat.isUserChat() ? user.toString() : chat.toString();

        boolean isSubscribed = subscribers.isSubscribed(id);

        if (!isSubscribed) {
            subscribers.subscribe(name, id);
            messageText = botStrings.get("message.sub.success");
        } else {
            messageText = botStrings.get("message.sub.alreadysub");
        }

        bot.sendMessage(chat.getId(), messageText);
    }
}
