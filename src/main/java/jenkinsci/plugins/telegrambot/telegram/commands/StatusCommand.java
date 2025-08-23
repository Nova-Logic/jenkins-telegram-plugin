package jenkinsci.plugins.telegrambot.telegram.commands;

import jenkins.model.GlobalConfiguration;
import jenkinsci.plugins.telegrambot.TelegramBotGlobalConfiguration;
import jenkinsci.plugins.telegrambot.users.Subscribers;
import jenkinsci.plugins.telegrambot.users.UserApprover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import jenkinsci.plugins.telegrambot.telegram.TelegramBot;

public class StatusCommand extends AbstractBotCommand {

    private static final TelegramBotGlobalConfiguration CONFIG = GlobalConfiguration.all().get(TelegramBotGlobalConfiguration.class);

    private static final String LOG_TAG = "/status";
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusCommand.class);

    public StatusCommand() {
        super("status", "command.status");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String[] arguments) {
        LOGGER.info("Executing status command for user: {}", user.getId());
        
        Subscribers subscribers = Subscribers.getInstance();
        String messageText;

        Long id = chat.getId();

        boolean isSubscribed = subscribers.isSubscribed(id);

        if (isSubscribed) {
            boolean isApproved = subscribers.isApproved(id);

            if (CONFIG.getApprovalType() == UserApprover.ApprovalType.ALL) {
                messageText = botStrings.get("message.status.approved");
            } else {
                messageText = isApproved
                        ? botStrings.get("message.status.approved")
                        : botStrings.get("message.status.unapproved");
            }
        } else {
            messageText = botStrings.get("message.status.unsubscribed");
        }

        bot.sendMessage(chat.getId(), messageText);
    }
}
