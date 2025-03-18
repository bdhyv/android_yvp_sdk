package com.bible.android_yvp_sdk.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bible.android_yvp_sdk.R
import com.bible.android_yvp_sdk.model.BibleVerseResponse
import com.bible.android_yvp_sdk.repository.BibleVerseRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A custom view that displays a Bible verse with reference and text.
 * 
 * This view handles fetching the verse data, loading state, and error handling.
 * It displays the verse reference, text content, and translation information.
 * 
 * Usage example:
 * ```xml
 * <com.bible.android_yvp_sdk.ui.BibleVerseView
 *     android:id="@+id/bibleVerseView"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content" />
 * ```
 * 
 * In your activity or fragment:
 * ```kotlin
 * val bibleVerseView = findViewById<BibleVerseView>(R.id.bibleVerseView)
 * bibleVerseView.loadVerse("john 3:16")
 * ```
 */
class BibleVerseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val repository = BibleVerseRepository()
    private val tvReference: TextView
    private val tvVerseText: TextView 
    private val tvTranslation: TextView
    private val progressBar: ProgressBar

    companion object {
        private const val TAG = "BibleVerseView"
    }

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.bible_verse_view, this, true)
        tvReference = root.findViewById(R.id.tvReference)
        tvVerseText = root.findViewById(R.id.tvVerseText)
        tvTranslation = root.findViewById(R.id.tvTranslation)
        progressBar = root.findViewById(R.id.progressBar)
    }

    /**
     * Loads a Bible verse from the configured data source.
     * 
     * This method fetches the verse data from the Google Sheets CSV file,
     * displays a loading indicator while fetching, and handles success and error states.
     * 
     * @param reference The Bible verse reference to load (e.g. "john 3:16", "genesis 1:1")
     */
    fun loadVerse(reference: String) {
        Log.d(TAG, "Loading verse: $reference")
        showLoading(true)
        
        repository.getVerse(reference, object : Callback<BibleVerseResponse> {
            override fun onResponse(
                call: Call<BibleVerseResponse>,
                response: Response<BibleVerseResponse>
            ) {
                Log.d(TAG, "Response received: ${response.code()}")
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { 
                        Log.d(TAG, "Verse loaded successfully: ${it.reference}")
                        displayVerse(it)
                    } ?: run {
                        Log.e(TAG, "Response successful but body is null")
                        showError()
                    }
                } else {
                    Log.e(TAG, "Error response: ${response.code()} - ${response.errorBody()?.string()}")
                    showError()
                }
            }

            override fun onFailure(call: Call<BibleVerseResponse>, t: Throwable) {
                Log.e(TAG, "Network failure", t)
                showLoading(false)
                showError()
            }
        })
    }

    /**
     * Displays the verse data in the view.
     *
     * @param verseResponse The Bible verse response to display
     */
    private fun displayVerse(verseResponse: BibleVerseResponse) {
        tvReference.text = verseResponse.reference
        tvVerseText.text = verseResponse.text.trim()
        tvTranslation.text = verseResponse.translationName
    }

    /**
     * Shows or hides the loading indicator and verse content.
     *
     * @param isLoading Whether the verse is currently loading
     */
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        tvReference.visibility = if (isLoading) View.GONE else View.VISIBLE
        tvVerseText.visibility = if (isLoading) View.GONE else View.VISIBLE
        tvTranslation.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    /**
     * Shows an error message when verse loading fails.
     */
    private fun showError() {
        Toast.makeText(context, R.string.error_loading_verse, Toast.LENGTH_SHORT).show()
    }
} 