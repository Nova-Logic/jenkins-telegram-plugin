package jenkinsci.plugins.telegrambot.telegram.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import jenkinsci.plugins.telegrambot.telegram.TelegramBot;

public class StartCommand extends AbstractBotCommand {

    private static final String LOG_TAG = "/start";
    private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

    public StartCommand() {
        super("start", "command.start");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String[] arguments) {
        LOGGER.info("Executing start command for user: {}", user.getId());
        bot.sendMessage(chat.getId(), botStrings.get("message.help"));
    }
}
