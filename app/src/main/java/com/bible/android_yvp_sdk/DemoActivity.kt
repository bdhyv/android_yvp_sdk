package com.bible.android_yvp_sdk

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.bible.android_yvp_sdk.ui.YvpBibleVerseView

/**
 * Demo activity that showcases the YvpBibleVerseView functionality.
 */
class DemoActivity : AppCompatActivity() {
    
    private lateinit var yvpBibleVerseView: YvpBibleVerseView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnRefresh: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        
        yvpBibleVerseView = findViewById(R.id.yvpBibleVerseView)
        radioGroup = findViewById(R.id.radioGroup)
        btnRefresh = findViewById(R.id.btnRefresh)
        
        btnRefresh.setOnClickListener {
            refreshVerse()
        }
    }
    
    private fun refreshVerse() {
        val translationId = when (radioGroup.checkedRadioButtonId) {
            R.id.rbNIV -> 111 // NIV
            R.id.rbESV -> 59  // ESV
            R.id.rbKJV -> 1   // KJV
            else -> 111       // Default to NIV
        }
        
        yvpBibleVerseView.loadVerse(translationId)
    }
} 