# Bible Verse SDK for Android

This Android SDK provides a simple way to integrate Bible verses into your Android applications. It includes two main view components:

1. **BibleVerseView** - A basic Bible verse display component
2. **YvpBibleVerseView** - A YouVersion-powered Bible verse component with authentication and additional features

## Features

- Display Bible verses with reference and text
- Basic BibleVerseView: Fetch verses from a Google Sheets document
- Advanced YvpBibleVerseView: Integration with YouVersion API, user authentication, and multiple translations
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
    implementation 'com.github.bdhyv:android_yvp_sdk:1.0.0'
}
```

## Usage

### Option 1: Basic BibleVerseView

The `BibleVerseView` is a simple component that displays a Bible verse with its reference and text.

#### XML Layout

Add the BibleVerseView to your layout:

```xml
<com.bible.android_yvp_sdk.ui.BibleVerseView
    android:id="@+id/bibleVerseView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

#### Kotlin Code

In your Activity or Fragment:

```kotlin
// Direct usage in an Activity
val bibleVerseView = findViewById<BibleVerseView>(R.id.bibleVerseView)
bibleVerseView.loadVerse("john 3:16")
```

#### Using with Fragment

```kotlin
// Using the BibleVerseFragment in your activity
val fragment = BibleVerseFragment.newInstance("john 3:16")
supportFragmentManager.beginTransaction()
    .replace(R.id.fragmentContainer, fragment)
    .commit()
```

#### Jetpack Compose Integration

```kotlin
// Bible verse text with BibleVerseView
AndroidView(
    factory = { context ->
        com.bible.android_yvp_sdk.ui.BibleVerseView(context).apply {
            loadVerse("john 3:16")
        }
    },
    modifier = Modifier.fillMaxWidth()
)
```

### Option 2: YouVersion-Powered YvpBibleVerseView

The `YvpBibleVerseView` is an advanced component that integrates with the YouVersion API, providing features like user authentication and multiple translations.

#### XML Layout

Add the YvpBibleVerseView to your layout:

```xml
<com.bible.android_yvp_sdk.ui.YvpBibleVerseView
    android:id="@+id/yvpBibleVerseView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

#### Kotlin Code

In your Activity or Fragment:

```kotlin
// Direct usage in an Activity
val yvpBibleVerseView = findViewById<YvpBibleVerseView>(R.id.yvpBibleVerseView)
// The view will automatically load verse of the day with default translation (NIV)

// To specify a translation ID:
yvpBibleVerseView.loadVerse(translationId = 59) // ESV translation
```

#### Using with Fragment

```kotlin
// Using the YvpBibleVerseFragment in your activity
val fragment = YvpBibleVerseFragment.newInstance(translationId = 111) // NIV translation
supportFragmentManager.beginTransaction()
    .replace(R.id.fragmentContainer, fragment)
    .commit()
```

#### Jetpack Compose Integration

```kotlin
// YouVersion-powered Bible verse component
AndroidView(
    factory = { context ->
        com.bible.android_yvp_sdk.ui.YvpBibleVerseView(context)
        // The view automatically loads verse of the day in the default translation
    },
    modifier = Modifier.fillMaxWidth()
)

// With specific translation
AndroidView(
    factory = { context ->
        com.bible.android_yvp_sdk.ui.YvpBibleVerseView(context).apply {
            loadVerse(translationId = 59) // ESV translation
        }
    },
    modifier = Modifier.fillMaxWidth()
)
```

## Translation IDs

Common translation IDs for YvpBibleVerseView:
- 111: NIV (New International Version)
- 59: ESV (English Standard Version)
- 1: KJV (King James Version)

## Sample App

A sample app is included in the repository to demonstrate how to use both components.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 