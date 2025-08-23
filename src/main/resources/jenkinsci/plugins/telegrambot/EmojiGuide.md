# Emoji Support in Jenkins Telegram Bot

The Jenkins Telegram Bot supports emojis to make your build notifications more engaging and visually appealing!

## How to Use Emojis

### Method 1: Direct Unicode Emojis
You can use actual emoji characters directly in your messages:
```
Build finished! ✅ SUCCESS
```

### Method 2: Emoji Placeholders
Use placeholder syntax that gets automatically converted to emojis:
```
Build finished! :success: STATUS
```

## Available Emoji Placeholders

### Build Status
- `:success:` → ✅ (Green checkmark)
- `:failure:` → ❌ (Red X)  
- `:unstable:` → ⚠️ (Warning)
- `:aborted:` → 🚫 (Prohibited)
- `:not_built:` → ⚪ (White circle)

### Build Process
- `:building:` → 🔄 (Arrows in circle)
- `:finished:` → 🏁 (Checkered flag)
- `:started:` → ▶️ (Play button)
- `:stopped:` → ⏹️ (Stop button)

### File Types
- `:document:` → 📄 (Document)
- `:image:` → 🖼️ (Picture frame)
- `:video:` → 🎥 (Movie camera)
- `:audio:` → 🎵 (Musical note)
- `:archive:` → 📦 (Package)
- `:log:` → 📝 (Memo)

### Development
- `:rocket:` → 🚀 (Rocket)
- `:gear:` → ⚙️ (Gear)
- `:hammer:` → 🔨 (Hammer)
- `:wrench:` → 🔧 (Wrench)
- `:chart:` → 📊 (Bar chart)
- `:clock:` → 🕐 (Clock)
- `:calendar:` → 📅 (Calendar)
- `:branch:` → 🌿 (Herb)
- `:commit:` → 💾 (Floppy disk)
- `:merge:` → 🔀 (Twisted arrows)

### Notifications
- `:bell:` → 🔔 (Bell)
- `:fire:` → 🔥 (Fire)
- `:sparkles:` → ✨ (Sparkles)
- `:tada:` → 🎉 (Party popper)
- `:thumbs_up:` → 👍 (Thumbs up)
- `:thumbs_down:` → 👎 (Thumbs down)
- `:eyes:` → 👀 (Eyes)
- `:robot:` → 🤖 (Robot)

### Jenkins Specific
- `:jenkins:` → 👷 (Construction worker)
- `:pipeline:` → 🔗 (Link)
- `:test:` → 🧪 (Test tube)
- `:bug:` → 🐛 (Bug)

## Example Messages

### Basic Build Notification
```
:jenkins: Build #${BUILD_NUMBER} :finished:
:success: Status: SUCCESS
:clock: Duration: 2m 30s
```

### File Upload with Emoji
```
:archive: Artifacts ready! 
Build #${BUILD_NUMBER} packages are available for download.
```

### Pipeline Status
```
:pipeline: Pipeline "${JOB_NAME}" 
:building: Currently running step: Tests
:test: Running unit tests...
```

### Enhanced Build Messages
```
:rocket: **Deployment Complete!**
:success: Build #${BUILD_NUMBER} deployed successfully
:chart: Coverage: 85%
:clock: Total time: 5m 12s
:tada: Ready for testing!
```

## Tips

1. **Combine with Token Macros**: Emojis work with all Jenkins token macros like `${BUILD_NUMBER}`, `${JOB_NAME}`, etc.

2. **File Type Auto-Detection**: When sending files, appropriate emojis are automatically added based on file extension.

3. **Markdown Support**: You can combine emojis with Markdown formatting:
   ```
   :success: **Build Successful!** 
   *Duration*: :clock: 1m 30s
   ```

4. **Status-Based Emojis**: The bot can automatically choose emojis based on build results when using utility methods.

## Configuration

Emojis are enabled by default and work in:
- Build step messages
- Post-build notifications  
- File upload captions
- Bot command responses
- Custom messages

No additional configuration required - just start using emoji placeholders in your messages!