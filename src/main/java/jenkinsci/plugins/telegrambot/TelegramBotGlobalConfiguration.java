package jenkinsci.plugins.telegrambot;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import jenkinsci.plugins.telegrambot.telegram.TelegramBotRunner;
import jenkinsci.plugins.telegrambot.users.Subscribers;
import jenkinsci.plugins.telegrambot.users.User;
import jenkinsci.plugins.telegrambot.users.UserApprover;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

/**
 * This class if user for the storing global plugin configuration.
 */
@Extension
public class TelegramBotGlobalConfiguration extends GlobalConfiguration {

    final static String PLUGIN_DISPLAY_NAME = "TelegramBot";
    private final Map<String, String> botStrings;

    private Boolean shouldLogToConsole = Boolean.TRUE;
    private Secret botToken;  // Changed to Secret for encryption
    private String botName;
    private UserApprover.ApprovalType approvalType;
    private Set<User> users;

    /**
     * Called when Jenkins is starting and it's config is loading
     */
    public TelegramBotGlobalConfiguration() {
        // Enhanced bot strings with emoji support
        Map<String, String> strings = new HashMap<>();
        strings.put("command.help", ":robot: Get help message");
        strings.put("message.help", ":jenkins: **Jenkins Bot Help**\n\n" +
                                   ":sparkles: Available Commands:\n" +
                                   "• `/start` - :rocket: Start using the bot\n" +
                                   "• `/help` - :gear: Show this help message\n" +
                                   "• `/sub` - :bell: Subscribe to notifications\n" +
                                   "• `/unsub` - :bell: Unsubscribe from notifications\n" +
                                   "• `/status` - :eyes: Check subscription status\n\n" +
                                   ":tada: Emoji support is enabled! Use placeholders like `:success:`, `:failure:`, `:building:`");
        strings.put("message.noncommand", ":thinking: I don't understand that. Use `/help` for available commands");
        strings.put("message.start", ":wave: Welcome to Jenkins Bot! :robot:\n\n" +
                                   "Use `/help` to see available commands or `/sub` to subscribe to build notifications.");
        strings.put("message.subscribed", ":bell: You're now subscribed to build notifications! :tada:");
        strings.put("message.unsubscribed", ":bell: You've been unsubscribed from notifications. :wave:");
        strings.put("message.already_subscribed", ":bell: You're already subscribed to notifications!");
        strings.put("message.not_subscribed", ":bell: You're not currently subscribed to notifications.");
        botStrings = Collections.unmodifiableMap(strings);
        
        // Load saved configuration
        load();
        
        // Auto-start bot if configuration exists (fix for issue #2)
        startBotIfConfigured();
    }

    /**
     * Called after configuration is saved - start bot with new settings
     */
    private void startBotIfConfigured() {
        String plainToken = botToken != null ? botToken.getPlainText() : null;
        if (botName != null && !botName.isEmpty() && plainToken != null && !plainToken.isEmpty()) {
            // Set up subscribers on first configuration
            if (users == null) {
                users = new HashSet<>();
                Subscribers.getInstance().setUsers(users);
                Subscribers.getInstance().addObserver(this::onSubscribersUpdate);
            }
            TelegramBotRunner.getInstance().runBot(botName, plainToken);
        }
    }

    private void onSubscribersUpdate(Observable o, Object arg) {
        users = Subscribers.getInstance().getUsers();
        save();
    }

    public FormValidation doCheckMessage(@QueryParameter String value) throws IOException, ServletException {
        return value.length() == 0 ? FormValidation.error("Please set a message") : FormValidation.ok();
    }

    public boolean isApplicable(Class<? extends AbstractProject> clazz) {
        return true;
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return PLUGIN_DISPLAY_NAME;
    }

    public Map<String, String> getBotStrings() {
        return botStrings;
    }

    public Boolean isShouldLogToConsole() {
        return shouldLogToConsole;
    }
    
    // Getter for Jelly checkbox binding (must match field name exactly)
    public boolean getShouldLogToConsole() {
        return shouldLogToConsole != null ? shouldLogToConsole : true;
    }

    @DataBoundSetter
    public void setShouldLogToConsole(Boolean shouldLogToConsole) {
        this.shouldLogToConsole = shouldLogToConsole;
        save();
    }

    /**
     * Get bot token as string for UI binding (Jenkins handles encryption/decryption)
     */
    public String getBotToken() {
        return botToken != null ? botToken.getPlainText() : null;
    }

    /**
     * Get encrypted Secret object for secure storage
     */
    public Secret getBotTokenSecret() {
        return botToken;
    }

    @DataBoundSetter
    public void setBotToken(String botToken) {
        // Store as encrypted Secret instead of plaintext
        this.botToken = Secret.fromString(botToken);
        save();
        startBotIfConfigured();
    }

    public String getBotName() {
        return botName;
    }

    @DataBoundSetter
    public void setBotName(String botName) {
        this.botName = botName;
        save();
        startBotIfConfigured();
    }

    public Set<User> getUsers() {
        return users;
    }

    public UserApprover.ApprovalType getApprovalType() {
        return approvalType != null ? approvalType : UserApprover.ApprovalType.ALL;
    }

    @DataBoundSetter
    public void setApprovalType(UserApprover.ApprovalType approvalType) {
        this.approvalType = approvalType;
        save();
    }

}
