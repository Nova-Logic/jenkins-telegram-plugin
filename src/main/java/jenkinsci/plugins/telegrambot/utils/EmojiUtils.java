package jenkinsci.plugins.telegrambot.utils;

import hudson.model.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling emoji support in Telegram messages
 */
public class EmojiUtils {

    // Jenkins build status emojis
    public static final String SUCCESS = "✅";
    public static final String FAILURE = "❌";
    public static final String UNSTABLE = "⚠️";
    public static final String ABORTED = "🚫";
    public static final String NOT_BUILT = "⚪";
    
    // Jenkins build process emojis
    public static final String BUILDING = "🔄";
    public static final String FINISHED = "🏁";
    public static final String STARTED = "▶️";
    public static final String STOPPED = "⏹️";
    
    // File type emojis
    public static final String DOCUMENT = "📄";
    public static final String IMAGE = "🖼️";
    public static final String VIDEO = "🎥";
    public static final String AUDIO = "🎵";
    public static final String ARCHIVE = "📦";
    public static final String LOG = "📝";
    
    // Development process emojis
    public static final String ROCKET = "🚀";
    public static final String GEAR = "⚙️";
    public static final String HAMMER = "🔨";
    public static final String WRENCH = "🔧";
    public static final String CHART = "📊";
    public static final String CLOCK = "🕐";
    public static final String CALENDAR = "📅";
    public static final String BRANCH = "🌿";
    public static final String COMMIT = "💾";
    public static final String MERGE = "🔀";
    
    // Notification emojis
    public static final String BELL = "🔔";
    public static final String FIRE = "🔥";
    public static final String SPARKLES = "✨";
    public static final String TADA = "🎉";
    public static final String THUMBS_UP = "👍";
    public static final String THUMBS_DOWN = "👎";
    public static final String EYES = "👀";
    public static final String ROBOT = "🤖";
    
    // Special Jenkins emojis
    public static final String JENKINS = "👷";  // Construction worker represents Jenkins
    public static final String PIPELINE = "🔗";
    public static final String TEST_TUBE = "🧪";
    public static final String BUG = "🐛";
    
    private static final Map<String, String> EMOJI_PLACEHOLDERS = new HashMap<>();
    
    static {
        // Build status placeholders
        EMOJI_PLACEHOLDERS.put(":success:", SUCCESS);
        EMOJI_PLACEHOLDERS.put(":failure:", FAILURE);
        EMOJI_PLACEHOLDERS.put(":unstable:", UNSTABLE);
        EMOJI_PLACEHOLDERS.put(":aborted:", ABORTED);
        EMOJI_PLACEHOLDERS.put(":not_built:", NOT_BUILT);
        
        // Build process placeholders
        EMOJI_PLACEHOLDERS.put(":building:", BUILDING);
        EMOJI_PLACEHOLDERS.put(":finished:", FINISHED);
        EMOJI_PLACEHOLDERS.put(":started:", STARTED);
        EMOJI_PLACEHOLDERS.put(":stopped:", STOPPED);
        
        // File type placeholders
        EMOJI_PLACEHOLDERS.put(":document:", DOCUMENT);
        EMOJI_PLACEHOLDERS.put(":image:", IMAGE);
        EMOJI_PLACEHOLDERS.put(":video:", VIDEO);
        EMOJI_PLACEHOLDERS.put(":audio:", AUDIO);
        EMOJI_PLACEHOLDERS.put(":archive:", ARCHIVE);
        EMOJI_PLACEHOLDERS.put(":log:", LOG);
        
        // Development placeholders
        EMOJI_PLACEHOLDERS.put(":rocket:", ROCKET);
        EMOJI_PLACEHOLDERS.put(":gear:", GEAR);
        EMOJI_PLACEHOLDERS.put(":hammer:", HAMMER);
        EMOJI_PLACEHOLDERS.put(":wrench:", WRENCH);
        EMOJI_PLACEHOLDERS.put(":chart:", CHART);
        EMOJI_PLACEHOLDERS.put(":clock:", CLOCK);
        EMOJI_PLACEHOLDERS.put(":calendar:", CALENDAR);
        EMOJI_PLACEHOLDERS.put(":branch:", BRANCH);
        EMOJI_PLACEHOLDERS.put(":commit:", COMMIT);
        EMOJI_PLACEHOLDERS.put(":merge:", MERGE);
        
        // Notification placeholders
        EMOJI_PLACEHOLDERS.put(":bell:", BELL);
        EMOJI_PLACEHOLDERS.put(":fire:", FIRE);
        EMOJI_PLACEHOLDERS.put(":sparkles:", SPARKLES);
        EMOJI_PLACEHOLDERS.put(":tada:", TADA);
        EMOJI_PLACEHOLDERS.put(":thumbs_up:", THUMBS_UP);
        EMOJI_PLACEHOLDERS.put(":thumbs_down:", THUMBS_DOWN);
        EMOJI_PLACEHOLDERS.put(":eyes:", EYES);
        EMOJI_PLACEHOLDERS.put(":robot:", ROBOT);
        
        // Jenkins specific placeholders
        EMOJI_PLACEHOLDERS.put(":jenkins:", JENKINS);
        EMOJI_PLACEHOLDERS.put(":pipeline:", PIPELINE);
        EMOJI_PLACEHOLDERS.put(":test:", TEST_TUBE);
        EMOJI_PLACEHOLDERS.put(":bug:", BUG);
    }
    
    /**
     * Get emoji for Jenkins build result
     */
    public static String getStatusEmoji(Result result) {
        if (result == null) {
            return NOT_BUILT;
        }
        
        if (result == Result.SUCCESS) {
            return SUCCESS;
        } else if (result == Result.FAILURE) {
            return FAILURE;
        } else if (result == Result.UNSTABLE) {
            return UNSTABLE;
        } else if (result == Result.ABORTED) {
            return ABORTED;
        } else if (result == Result.NOT_BUILT) {
            return NOT_BUILT;
        }
        
        return NOT_BUILT; // Default fallback
    }
    
    /**
     * Get emoji for file type based on file extension
     */
    public static String getFileTypeEmoji(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return DOCUMENT;
        }
        
        String extension = getFileExtension(fileName).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "webp":
            case "svg":
                return IMAGE;
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
            case "webm":
                return VIDEO;
            case "mp3":
            case "wav":
            case "flac":
            case "ogg":
            case "m4a":
                return AUDIO;
            case "zip":
            case "tar":
            case "gz":
            case "rar":
            case "7z":
            case "jar":
            case "war":
                return ARCHIVE;
            case "log":
            case "txt":
                return LOG;
            default:
                return DOCUMENT;
        }
    }
    
    /**
     * Replace emoji placeholders in text with actual emojis
     * Example: "Build :success:" becomes "Build ✅"
     */
    public static String replaceEmojiPlaceholders(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String result = text;
        for (Map.Entry<String, String> entry : EMOJI_PLACEHOLDERS.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    /**
     * Create a formatted build status message with emojis
     */
    public static String formatBuildMessage(String jobName, int buildNumber, Result result, long duration) {
        String statusEmoji = getStatusEmoji(result);
        String statusText = result != null ? result.toString() : "NOT_BUILT";
        String durationText = formatDuration(duration);
        
        return String.format("%s **Build %s #%d**%n" +
                           "%s Status: %s%n" +
                           "%s Duration: %s",
                           JENKINS, jobName, buildNumber,
                           statusEmoji, statusText,
                           CLOCK, durationText);
    }
    
    /**
     * Create a quick status message template that users can customize
     */
    public static String getQuickStatusTemplate() {
        return JENKINS + " Build ${JOB_NAME} #${BUILD_NUMBER} " + FINISHED + "\n" +
               ":status: Status: ${BUILD_RESULT}\n" +
               CLOCK + " Duration: ${BUILD_DURATION}\n" +
               CALENDAR + " Started: ${BUILD_TIMESTAMP}";
    }
    
    /**
     * Create emoji-rich build notification templates for common scenarios
     */
    public static class Templates {
        public static final String SUCCESS_BUILD = 
            TADA + " **Build Successful!** " + SUCCESS + "\n" +
            JENKINS + " Job: ${JOB_NAME} #${BUILD_NUMBER}\n" +
            CLOCK + " Duration: ${BUILD_DURATION}\n" +
            ROCKET + " Ready for deployment!";
            
        public static final String FAILED_BUILD = 
            FIRE + " **Build Failed!** " + FAILURE + "\n" +
            JENKINS + " Job: ${JOB_NAME} #${BUILD_NUMBER}\n" +
            CLOCK + " Duration: ${BUILD_DURATION}\n" +
            WRENCH + " Check the logs for details.";
            
        public static final String UNSTABLE_BUILD = 
            UNSTABLE + " **Build Unstable** " + UNSTABLE + "\n" +
            JENKINS + " Job: ${JOB_NAME} #${BUILD_NUMBER}\n" +
            TEST_TUBE + " Some tests may have failed\n" +
            EYES + " Please review the results.";
            
        public static final String DEPLOYMENT_SUCCESS = 
            ROCKET + " **Deployment Complete!** " + SUCCESS + "\n" +
            GEAR + " Environment: ${ENV_NAME}\n" +
            BRANCH + " Branch: ${GIT_BRANCH}\n" +
            SPARKLES + " Application is live!";
            
        public static final String FILE_ARTIFACT = 
            ARCHIVE + " **Build Artifacts Ready** " + FINISHED + "\n" +
            JENKINS + " Build: ${JOB_NAME} #${BUILD_NUMBER}\n" +
            DOCUMENT + " Files generated successfully\n" +
            THUMBS_UP + " Ready for download!";
    }
    
    /**
     * Get common emoji combinations for different notification types
     */
    public static class Combinations {
        public static final String BUILD_START = JENKINS + " " + STARTED;
        public static final String BUILD_SUCCESS = SUCCESS + " " + TADA;  
        public static final String BUILD_FAILED = FAILURE + " " + FIRE;
        public static final String BUILD_UNSTABLE = UNSTABLE + " " + EYES;
        public static final String DEPLOYMENT = ROCKET + " " + SPARKLES;
        public static final String TESTING = TEST_TUBE + " " + GEAR;
        public static final String FILE_READY = ARCHIVE + " " + THUMBS_UP;
    }
    
    /**
     * Create a formatted file notification message with emojis
     */
    public static String formatFileMessage(String fileName, String description) {
        String fileEmoji = getFileTypeEmoji(fileName);
        return String.format("%s **%s**%n%s %s", 
                           fileEmoji, fileName,
                           DOCUMENT, description != null ? description : "File sent");
    }
    
    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1);
    }
    
    private static String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
}