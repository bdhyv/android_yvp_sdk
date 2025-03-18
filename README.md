# Bible Verse SDK for Android

This Android SDK provides a simple way to integrate Bible verses into your Android applications.

## Features

- Display Bible verses with reference and text
- Fetch verses from a Google Sheets document
- Customizable UI components
- Simple integration

## Requirements

- Android API level 21 (Android 5.0) or higher
- Support for Kotlin

## Installation

### Step 1: Add the repository

Add the JitPack repository to your root `build.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

Add the dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.yourusername:android_yvp_sdk:1.0.0'
}
```

## Usage

### XML Layout

Add the BibleVerseView to your layout:

```xml
<com.bible.android_yvp_sdk.ui.BibleVerseView
    android:id="@+id/bibleVerseView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

### Loading a Verse

In your Activity or Fragment:

```kotlin
// Direct usage in an Activity
val bibleVerseView = findViewById<BibleVerseView>(R.id.bibleVerseView)
bibleVerseView.loadVerse("john 3:16")
```

### Using the Fragment

```kotlin
// Using the Fragment in your activity
val fragment = BibleVerseFragment.newInstance("john 3:16")
supportFragmentManager.beginTransaction()
    .replace(R.id.fragmentContainer, fragment)
    .commit()
```

### Customization

You can customize the verse display by modifying the `bible_verse_view.xml` layout in your project.

## Configuration

By default, the SDK fetches verses from a predefined Google Sheets document. To use your own data source:

1. Create a Google Sheet with columns for the verse reference and text
2. Publish your sheet to the web as CSV
3. Configure the SDK to use your sheet URL:

```kotlin
// In your Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BibleVerseRepository.configure(
            sheetsUrl = "https://docs.google.com/spreadsheets/d/YOUR_SHEET_ID/pub?output=csv"
        )
    }
}
```

## Sample App

A sample app is included in the repository to demonstrate how to use the SDK.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 