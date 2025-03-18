package com.bible.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bible.android_yvp_sdk.ui.BibleVerseView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bibleVerseView = findViewById<BibleVerseView>(R.id.bibleVerseView)
        bibleVerseView.loadVerse("john 3:16")
    }
} 