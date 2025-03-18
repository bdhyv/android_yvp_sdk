package com.bible.sample

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.bible.android_yvp_sdk.ui.BibleVerseView
import com.bible.android_yvp_sdk.ui.YvpBibleVerseView

class MainActivity : AppCompatActivity() {
    
    private lateinit var bibleVerseView: BibleVerseView
    private lateinit var yvpBibleVerseView: YvpBibleVerseView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnRefresh: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Find views
        bibleVerseView = findViewById(R.id.bibleVerseView)
        yvpBibleVerseView = findViewById(R.id.yvpBibleVerseView)
        radioGroup = findViewById(R.id.radioGroup)
        btnRefresh = findViewById(R.id.btnRefresh)
        
        // Load the original BibleVerseView with a reference
        bibleVerseView.loadVerse("john 3:16")
        
        // Set up YVP refresh button
        btnRefresh.setOnClickListener {
            refreshYvpVerse()
        }
    }
    
    /**
     * Refresh the YouVersion verse with the selected translation
     */
    private fun refreshYvpVerse() {
        val translationId = when (radioGroup.checkedRadioButtonId) {
            R.id.rbNIV -> 111 // NIV
            R.id.rbESV -> 59  // ESV
            R.id.rbKJV -> 1   // KJV
            else -> 111       // Default to NIV
        }
        
        yvpBibleVerseView.loadVerse(translationId)
    }
} 