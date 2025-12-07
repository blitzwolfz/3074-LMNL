# LMNL - Less Noise, More Connection

An Android social media application designed to promote intentional and mindful social media usage through daily interaction limits. LMNL encourages users to engage thoughtfully by implementing daily caps on posts, likes, comments, and feed views.

## Overview

LMNL is a standalone mobile application focused on intentional social media usage. User data, posts, and interactions are stored using SQLite databases. The app enforces daily usage limits to encourage meaningful connections over mindless scrolling.

## Features

### Authentication & User Management
- User registration with email and username validation
- Secure login with session persistence
- Quick login feature that remembers the last used email
- User profiles with editable information (name, email, bio, website)
- Logout functionality with confirmation dialogs

### Post & Feed System
- Main feed displaying posts from all users
- Create new posts with character limits
- Real-time search and filtering by content and username
- View individual post details
- Posts ordered chronologically (newest first)
- Posts display author username and formatted timestamps

### Social Interactions
- **Likes**: Like and unlike posts with daily limit tracking
- **Comments**: Add comments to posts with full comment threading
- View all comments on individual posts
- Social interaction limits reset daily

### Daily Limits
The app enforces the following daily limits to promote mindful usage:
- **Feed Views**: 10 posts per day
- **Posts**: 5 posts per day
- **Likes**: 5 likes per day
- **Comments**: 5 comments per day

Limits are tracked per user and reset automatically each day.

### Additional Features
- Splash screen with video background
- About/Mission screen explaining the app's philosophy
- Bottom navigation for easy access to Feed, Home, and Profile
- Material Design 3 UI components
- Edge-to-edge layouts with modern Android design

## Technical Stack

### Development Environment
- **Language**: Java 11
- **Build System**: Gradle (Kotlin DSL)
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 36
- **Compile SDK**: 36

### Libraries & Dependencies
- **AndroidX AppCompat**: 1.7.0
- **Material Design Components**: 1.12.0
- **ConstraintLayout**: 2.1.4
- **Lottie Animations**: 6.4.0
- **RecyclerView**: AndroidX
- **JUnit**: 4.13.2 (Testing)
- **Espresso**: 3.6.1 (UI Testing)

### Architecture
- **UI Pattern**: Activity-based navigation with explicit Intents
- **Database**: SQLite with SQLiteOpenHelper
- **Data Storage**: Two separate databases (`users.db` and `feed.db`)
- **Session Management**: SharedPreferences
- **View Binding**: Direct findViewById() calls

## Project Structure

```
com.example.lmnl/
├── auth/                          # Authentication & Session Management
│   ├── LoginActivity.java
│   ├── RegisterActivity.java
│   └── SessionManager.java
├── post/                          # Posts & Social Interactions
│   ├── FeedActivity.java
│   ├── CreatePostActivity.java
│   ├── PostDetailActivity.java
│   ├── Post.java
│   ├── Comment.java
│   ├── PostsAdapter.java
│   ├── CommentsAdapter.java
│   ├── PostsDbHelper.java
│   ├── PostContract.java
│   └── DailyLimitsManager.java
├── user/                          # User Profile Management
│   ├── ProfileActivity.java
│   ├── EditProfileActivity.java
│   ├── CreateProfileActivity.java
│   ├── User.java
│   ├── UserDbHelper.java
│   └── UserContract.java
├── MainActivity.java              # Splash Screen (App Entry Point)
└── AboutActivity.java             # Mission/Philosophy Screen
```

### Database Schema

**users.db**
- **users** table: `id`, `username`, `full_name`, `email`, `password`, `bio`, `website`

**feed.db**
- **posts** table: `id`, `username`, `content`, `created_at`
- **likes** table: `id`, `post_id`, `username`, `created_at`
- **comments** table: `id`, `post_id`, `username`, `content`, `created_at`

## Installation & Setup

### Prerequisites
- Android Studio (latest version recommended)
- JDK 11 or higher
- Android SDK with API level 26 or higher
- An Android device or emulator running Android 8.0 (Oreo) or higher

### Setup Steps

1. **Clone or Download the Repository**
   ```bash
   cd path/to/project
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory and select it

3. **Sync Gradle**
   - Android Studio should automatically sync Gradle dependencies
   - If not, click "Sync Project with Gradle Files" in the toolbar

4. **Connect Device or Start Emulator**
   - Connect an Android device via USB with USB debugging enabled, or
   - Start an Android Virtual Device (AVD) from AVD Manager

## Build & Run

### Build Commands

```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device/emulator
./gradlew installDebug

# Clean build
./gradlew clean
```

### Running the App

**From Android Studio:**
1. Click the "Run" button (green play icon) or press Shift+F10
2. Select your target device/emulator
3. The app will build, install, and launch automatically

**From Command Line:**
```bash
# Install and run on connected device
./gradlew installDebug
adb shell am start -n com.example.lmnl/.MainActivity
```

## Testing

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.example.lmnl.SpecificTestClass
```

### Instrumented Tests
```bash
# Run all instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run specific instrumented test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.lmnl.ExampleInstrumentedTest
```

### Code Quality
```bash
# Run lint checks
./gradlew lint

# Generate lint report (outputs to app/build/reports/lint-results.html)
./gradlew lintDebug
```

## Usage

1. **First Launch**: The app opens to a splash screen with a video background
2. **Registration**: New users tap "Get Started" and register with email, username, and password
3. **Login**: Returning users can log in with their email and password
4. **Feed**: After login, users see the main feed with posts from all users
5. **Create Post**: Tap the floating action button (+) to create a new post
6. **Interact**: Like and comment on posts (subject to daily limits)
7. **Search**: Use the search bar to filter posts by content or username
8. **Profile**: Access profile from bottom navigation to view/edit personal information
9. **Daily Limits**: Monitor your usage through the app's daily limit tracking

## Design Philosophy

LMNL emphasizes:
- **Privacy**: Secure data management
- **Intentionality**: Daily limits encourage thoughtful engagement
- **Simplicity**: Focused feature set without algorithmic feeds or endless scrolling
- **Connection**: Quality interactions over quantity metrics

## Known Limitations

- Passwords are currently stored in plain text (suitable for academic demo, not production)
- No image/video post support (text-only)
- Daily limits are tracked per device

## Authors

This project was developed as part of an academic course.

- **Samin Qureshi** - Student ID: 101534641
- **Pierre-Anthony Eid** - Student ID: 101529840
- **Soman Ahmad** - Student ID: 100974825

## License

This project is an academic assignment and is intended for educational purposes only.

---

**Note**: This application is designed for educational purposes to demonstrate Android development concepts including database management, UI design, and data persistence.
