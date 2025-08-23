package jenkinsci.plugins.telegrambot;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import jenkinsci.plugins.telegrambot.telegram.TelegramBotRunner;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TelegramBotFileSender extends Builder implements SimpleBuildStep {

    /**
     * The file path to send (supports Jenkins FilePath patterns)
     */
    private final String filePath;

    /**
     * Optional caption for the file (supports token macro expansion)
     */
    private final String caption;

    @DataBoundConstructor
    public TelegramBotFileSender(String filePath, String caption) {
        this.filePath = filePath;
        this.caption = caption;
    }

    @Extension
    public static class Descriptor extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Send file via " + TelegramBotGlobalConfiguration.PLUGIN_DISPLAY_NAME;
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public void perform(
            @Nonnull Run<?, ?> run,
            @Nonnull FilePath workspace,
            @Nonnull Launcher launcher,
            @Nonnull TaskListener taskListener) throws InterruptedException, IOException {

        if (filePath == null || filePath.trim().isEmpty()) {
            taskListener.getLogger().println("TelegramBot File Sender: No file path specified");
            return;
        }

        // Resolve file path relative to workspace
        FilePath targetFile = workspace.child(filePath);
        
        if (!targetFile.exists()) {
            taskListener.getLogger().println("TelegramBot File Sender: File does not exist: " + targetFile.getRemote());
            return;
        }

        if (targetFile.isDirectory()) {
            taskListener.getLogger().println("TelegramBot File Sender: Path is a directory, not a file: " + targetFile.getRemote());
            return;
        }

        // Send file using the TelegramBot instance
        TelegramBotRunner.getInstance().getBot()
                .telegramSendFile(targetFile, caption, run, taskListener);
    }
}