package jenkinsci.plugins.telegrambot.telegram;

import jenkins.model.GlobalConfiguration;
import jenkinsci.plugins.telegrambot.TelegramBotGlobalConfiguration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelegramBotRunner {
    private static TelegramBotRunner instance;

    private static final Logger LOG = Logger.getLogger(TelegramBot.class.getName());

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TelegramBotsLongPollingApplication botsApplication;

    private TelegramBot bot;
    private String botToken;
    private String botName;

    public synchronized static TelegramBotRunner getInstance() {
        if (instance == null) {
            instance = new TelegramBotRunner();
        }
        return instance;
    }

    public void runBot(String name, String token) {
        if (token == null || token.isEmpty() || name == null || name.isEmpty()) {
            LOG.log(Level.WARNING, "Bot name or token is empty, not starting bot");
            return;
        }
        botName = name;
        botToken = token;
        executor.submit(startBotTask);
    }

    public TelegramBot getBot() {
        return bot;
    }

    private final Runnable startBotTask = () -> {
        if (bot == null
                || !bot.getBotToken().equals(botToken)
                || !bot.getBotUsername().equals(botName)) {
            bot = new TelegramBot(botToken, botName);
            LOG.log(Level.INFO, "Bot was created");
        } else {
            LOG.log(Level.INFO, "There is no reason for bot recreating");
            return;
        }
        createBotSession();
    };

    private void createBotSession() {
        if (botsApplication != null) {
            LOG.info("Stopping previous bot session");
            try {
                botsApplication.close();
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Error stopping previous bot session", e);
            }
        }

        try {
            botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, bot);
            LOG.log(Level.INFO, "New bot session was registered");
        } catch (TelegramApiException e) {
            LOG.log(Level.SEVERE, "Telegram API error", e);
        }
    }
}
