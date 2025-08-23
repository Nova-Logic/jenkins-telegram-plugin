package jenkinsci.plugins.telegrambot.telegram;

import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.GlobalConfiguration;
import jenkinsci.plugins.telegrambot.TelegramBotGlobalConfiguration;
import jenkinsci.plugins.telegrambot.telegram.commands.*;
import jenkinsci.plugins.telegrambot.users.Subscribers;
import jenkinsci.plugins.telegrambot.utils.EmojiUtils;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private static final Logger LOG = Logger.getLogger(TelegramBot.class.getName());

    private static final TelegramBotGlobalConfiguration CONFIG = GlobalConfiguration.all().get(TelegramBotGlobalConfiguration.class);
    private static final Subscribers SUBSCRIBERS = Subscribers.getInstance();

    private final String token;
    private final String botUsername;
    private final TelegramClient telegramClient;


    public TelegramBot(String token, String botUsername) {
        this.token = token;
        this.botUsername = botUsername;
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    public void sendMessage(Long chatId, String message) {
        // Process emoji placeholders in direct messages too
        String processedMessage = EmojiUtils.replaceEmojiPlaceholders(message);
        
        final SendMessage sendMessageRequest = SendMessage.builder()
                .chatId(chatId.toString())
                .text(processedMessage)
                .parseMode("Markdown")
                .build();

        try {
            telegramClient.execute(sendMessageRequest);
        } catch (TelegramApiException e) {
            LOG.log(Level.SEVERE, String.format(
                    "TelegramBot: Error while sending message: %s%n%s", chatId, processedMessage), e);
        }
    }

    private static String expandMessage(String message, Run<?, ?> run, FilePath filePath, TaskListener taskListener)
            throws IOException, InterruptedException {

        try {
            // First expand token macros
            String expandedMessage = TokenMacro.expandAll(run, filePath, taskListener, message);
            
            // Then process emoji placeholders
            return EmojiUtils.replaceEmojiPlaceholders(expandedMessage);
        } catch (MacroEvaluationException e) {
            LOG.log(Level.SEVERE, "Error while expanding the message", e);
        }

        // Fallback: still process emojis even if token expansion fails
        return EmojiUtils.replaceEmojiPlaceholders(message);
    }

    public void sendMessage(
            Long chatId, String message, Run<?, ?> run, FilePath filePath, TaskListener taskListener)
            throws IOException, InterruptedException {

        final String expandedMessage = expandMessage(message, run, filePath, taskListener);

        try {
            if (chatId == null) {
                SUBSCRIBERS.getApprovedUsers()
                        .forEach(user -> this.sendMessage(user.getId(), expandedMessage));
            } else {
                sendMessage(chatId, expandedMessage);
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while sending the message", e);
        }

        if (CONFIG.isShouldLogToConsole()) taskListener.getLogger().println(expandedMessage);
    }

    public void sendMessage(
            String message, Run<?, ?> run, FilePath filePath, TaskListener taskListener)
            throws IOException, InterruptedException {

        sendMessage(null, message, run, filePath, taskListener);
    }

    /**
     * Send a file to a specific chat
     */
    public void telegramSendFile(Long chatId, File file, String caption) {
        if (file == null || !file.exists()) {
            LOG.log(Level.WARNING, "File is null or does not exist: " + file);
            return;
        }

        final InputFile inputFile = new InputFile(file);
        
        try {
            sendFileByType(chatId, inputFile, file.getName(), caption);
        } catch (TelegramApiException e) {
            LOG.log(Level.SEVERE, String.format(
                    "TelegramBot: Error while sending file: %s to chat: %s", file.getName(), chatId), e);
        }
    }

    /**
     * Send a file from Jenkins FilePath using direct stream (no temp files)
     */
    public void telegramSendFile(Long chatId, FilePath filePath, String caption, TaskListener taskListener) 
            throws IOException, InterruptedException {
        if (filePath == null || !filePath.exists()) {
            LOG.log(Level.WARNING, "FilePath is null or does not exist: " + filePath);
            return;
        }

        if (filePath.isDirectory()) {
            LOG.log(Level.WARNING, "Path is a directory, not a file: " + filePath.getRemote());
            return;
        }

        // Send file directly using InputStream - no temp file needed
        try (InputStream inputStream = filePath.read()) {
            final InputFile inputFile = new InputFile(inputStream, filePath.getName());
            
            // Process emoji placeholders in caption and add file type emoji
            String processedCaption = caption != null ? EmojiUtils.replaceEmojiPlaceholders(caption) : null;
            String fileTypeEmoji = EmojiUtils.getFileTypeEmoji(filePath.getName());
            
            // Enhance caption with file type emoji if caption is provided
            if (processedCaption != null && !processedCaption.isEmpty()) {
                processedCaption = fileTypeEmoji + " " + processedCaption;
            }
            
            sendFileByType(chatId, inputFile, filePath.getName(), processedCaption);
            
            if (CONFIG != null && CONFIG.isShouldLogToConsole() && taskListener != null) {
                taskListener.getLogger().println("Sent file to Telegram: " + filePath.getName());
            }
        } catch (TelegramApiException e) {
            LOG.log(Level.SEVERE, String.format(
                    "TelegramBot: Error while sending file: %s to chat: %s", filePath.getName(), chatId), e);
        }
    }

    /**
     * Send a file to all approved subscribers
     */
    public void telegramSendFile(FilePath filePath, String caption, Run<?, ?> run, TaskListener taskListener) 
            throws IOException, InterruptedException {
        
        final String expandedCaption = caption != null ? expandMessage(caption, run, filePath, taskListener) : null;
        
        try {
            SUBSCRIBERS.getApprovedUsers().forEach(user -> {
                try {
                    telegramSendFile(user.getId(), filePath, expandedCaption, taskListener);
                } catch (IOException | InterruptedException e) {
                    LOG.log(Level.SEVERE, "Error sending file to user: " + user.getId(), e);
                }
            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while sending file to subscribers", e);
        }
    }

    /**
     * Helper method to send file using appropriate Telegram method based on file extension
     */
    private void sendFileByType(Long chatId, InputFile inputFile, String fileName, String caption) throws TelegramApiException {
        final String fileExtension = getFileExtension(fileName).toLowerCase();
        
        switch (fileExtension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "webp":
                sendPhoto(chatId, inputFile, caption);
                break;
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
                sendVideo(chatId, inputFile, caption);
                break;
            case "mp3":
            case "wav":
            case "flac":
            case "ogg":
            case "m4a":
                sendAudio(chatId, inputFile, caption);
                break;
            default:
                sendDocument(chatId, inputFile, caption);
                break;
        }
    }

    private void sendDocument(Long chatId, InputFile document, String caption) throws TelegramApiException {
        final SendDocument.SendDocumentBuilder builder = SendDocument.builder()
                .chatId(chatId.toString())
                .document(document);
                
        if (caption != null && !caption.isEmpty()) {
            builder.caption(caption).parseMode("Markdown");
        }
        
        telegramClient.execute(builder.build());
    }

    private void sendPhoto(Long chatId, InputFile photo, String caption) throws TelegramApiException {
        final SendPhoto.SendPhotoBuilder builder = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(photo);
                
        if (caption != null && !caption.isEmpty()) {
            builder.caption(caption).parseMode("Markdown");
        }
        
        telegramClient.execute(builder.build());
    }

    private void sendVideo(Long chatId, InputFile video, String caption) throws TelegramApiException {
        final SendVideo.SendVideoBuilder builder = SendVideo.builder()
                .chatId(chatId.toString())
                .video(video);
                
        if (caption != null && !caption.isEmpty()) {
            builder.caption(caption).parseMode("Markdown");
        }
        
        telegramClient.execute(builder.build());
    }

    private void sendAudio(Long chatId, InputFile audio, String caption) throws TelegramApiException {
        final SendAudio.SendAudioBuilder builder = SendAudio.builder()
                .chatId(chatId.toString())
                .audio(audio);
                
        if (caption != null && !caption.isEmpty()) {
            builder.caption(caption).parseMode("Markdown");
        }
        
        telegramClient.execute(builder.build());
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1);
    }

    @Override
    public void consume(Update update) {
        if (update == null) {
            LOG.log(Level.WARNING, "Update is null");
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Chat chat = message.getChat();

            if (messageText.startsWith("/")) {
                handleCommand(messageText, chat, message);
            } else {
                handleNonCommandUpdate(update);
            }
        }
    }

    private void handleCommand(String commandText, Chat chat, Message message) {
        String[] parts = commandText.split(" ");
        String command = parts[0].toLowerCase();
        
        // Handle commands manually
        switch (command) {
            case "/start":
                new StartCommand().execute(this, message.getFrom(), chat, parts);
                break;
            case "/help":
                new HelpCommand().execute(this, message.getFrom(), chat, parts);
                break;
            case "/sub":
                new SubCommand().execute(this, message.getFrom(), chat, parts);
                break;
            case "/unsub":
                new UnsubCommand().execute(this, message.getFrom(), chat, parts);
                break;
            case "/status":
                new StatusCommand().execute(this, message.getFrom(), chat, parts);
                break;
            default:
                final String nonCommandMessage = CONFIG.getBotStrings().get("message.noncommand");
                sendMessage(chat.getId(), nonCommandMessage);
                break;
        }
    }

    private void handleNonCommandUpdate(Update update) {
        final String nonCommandMessage = CONFIG.getBotStrings()
                .get("message.noncommand");

        final Message message = update.getMessage();
        final Chat chat = message.getChat();

        if (chat.isUserChat()) {
            sendMessage(chat.getId(), nonCommandMessage);
            return;
        }

        final String text = message.getText();

        try {
            // Skip not direct messages in chats
            if (text.length() < 1 || text.charAt(0) != '@') return;
            final String[] tmp = text.split(" ");
            if (tmp.length < 2 || !botUsername.equals(tmp[0].substring(1, tmp[0].length()))) return;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Something bad happened while message processing", e);
            return;
        }

        sendMessage(chat.getId(), nonCommandMessage);
    }

    public String getBotToken() {
        return token;
    }

    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String toString() {
        return "TelegramBot{" + token + "}";
    }

}
