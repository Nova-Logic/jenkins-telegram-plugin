package jenkinsci.plugins.telegrambot.telegram.commands;

import jenkins.model.GlobalConfiguration;
import jenkinsci.plugins.telegrambot.TelegramBotGlobalConfiguration;
import jenkinsci.plugins.telegrambot.telegram.TelegramBot;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Map;

public abstract class AbstractBotCommand {
    private static final TelegramBotGlobalConfiguration CONFIG = GlobalConfiguration.all().get(TelegramBotGlobalConfiguration.class);

    protected final Map<String, String> botStrings;
    protected final String commandIdentifier;
    protected final String description;

    public AbstractBotCommand(final String commandIdentifier, final String descriptionKey) {
        this.commandIdentifier = commandIdentifier;
        this.description = CONFIG.getBotStrings().get(descriptionKey);
        this.botStrings = CONFIG.getBotStrings();
    }

    public abstract void execute(TelegramBot bot, User user, Chat chat, String[] arguments);

    public String getCommandIdentifier() {
        return commandIdentifier;
    }

    public String getDescription() {
        return description;
    }
}
