package com.bible.android_yvp_sdk.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.bible.android_yvp_sdk.R
import com.bible.android_yvp_sdk.model.YvpBibleVerseResponse
import com.bible.android_yvp_sdk.model.YvpUserResponse
import com.bible.android_yvp_sdk.repository.YvpBibleVerseRepository

/**
 * A custom view that displays a Bible verse from YouVersion with authentication.
 * 
 * This view handles authentication, fetching the verse data, loading state, and error handling.
 * Users need to authenticate before seeing the verse of the day.
 * 
 * Usage example:
 * ```xml
 * <com.bible.android_yvp_sdk.ui.YvpBibleVerseView
 *     android:id="@+id/yvpBibleVerseView"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content" />
 * ```
 * 
 * In your activity or fragment:
 * ```kotlin
 * val yvpBibleVerseView = findViewById<YvpBibleVerseView>(R.id.yvpBibleVerseView)
 * // The view will handle authentication and verse loading automatically
 * ```
 */
class YvpBibleVerseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val repository = YvpBibleVerseRepository()
    private val btnLogin: Button
    private val progressBar: ProgressBar
    private val verseContentLayout: LinearLayout
    private val tvReference: TextView
    private val tvVerseText: TextView
    private val tvTranslation: TextView
    private val tvError: TextView
    private val tvUserGreeting: TextView

    companion object {
        private const val TAG = "YvpBibleVerseView"
        private const val DEFAULT_TRANSLATION_ID = 111 // NIV by default
    }

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.yvp_bible_verse_view, this, true)
        btnLogin = root.findViewById(R.id.btnLogin)
        progressBar = root.findViewById(R.id.progressBar)
        verseContentLayout = root.findViewById(R.id.verseContentLayout)
        tvReference = root.findViewById(R.id.tvReference)
        tvVerseText = root.findViewById(R.id.tvVerseText)
        tvTranslation = root.findViewById(R.id.tvTranslation)
        tvError = root.findViewById(R.id.tvError)
        tvUserGreeting = root.findViewById(R.id.tvUserGreeting)

        btnLogin.text = context.getString(R.string.login_to_youversion)
        
        btnLogin.setOnClickListener {
            showLoginDialog()
        }

        // Load verse with static token immediately
        loadVerseWithStaticToken()
        
        // Check if already authenticated
        checkAuthenticationStatus()
    }

    /**
     * Loads the verse using the static token (no auth required)
     */
    private fun loadVerseWithStaticToken(translationId: Int = DEFAULT_TRANSLATION_ID) {
        Log.d(TAG, "Loading verse with static token, translation ID: $translationId")
        
        showLoading(true)
        
        repository.getVerseOfTheDayWithStaticToken(translationId, object : YvpBibleVerseRepository.VerseCallback {
            override fun onSuccess(verse: YvpBibleVerseResponse) {
                Log.d(TAG, "Verse loaded successfully with static token: ${verse.usfm}")
                showLoading(false)
                displayVerse(verse)
            }

            override fun onError(message: String) {
                Log.e(TAG, "Error loading verse with static token: $message")
                showLoading(false)
                showError(message)
            }
        })
    }

    /**
     * Shows the login dialog to authenticate the user.
     */
    private fun showLoginDialog() {
        val loginDialog = YvpLoginDialog(context, repository) { token ->
            // On successful login
            fetchUserInfo(token)
        }
        loginDialog.show()
    }

    /**
     * Fetches user information after successful login
     */
    private fun fetchUserInfo(token: String) {
        showLoading(true)
        
        repository.getUserInfo(token, object : YvpBibleVerseRepository.UserInfoCallback {
            override fun onSuccess(userInfo: YvpUserResponse) {
                showLoading(false)
                displayUserGreeting(userInfo)
            }

            override fun onError(message: String) {
                showLoading(false)
                showError("Failed to get user info: $message")
            }
        })
    }

    /**
     * Displays the user greeting with their name
     */
    private fun displayUserGreeting(userInfo: YvpUserResponse) {
        tvUserGreeting.text = "Hello, ${userInfo.firstName}"
        tvUserGreeting.visibility = View.VISIBLE
        btnLogin.visibility = View.GONE // Hide login button after successful login
    }

    /**
     * Checks if the user is authenticated and updates the UI accordingly.
     */
    private fun checkAuthenticationStatus() {
        if (repository.isAuthenticated()) {
            // If authenticated, fetch user info
            repository.getUserInfo(repository.authToken ?: "", object : YvpBibleVerseRepository.UserInfoCallback {
                override fun onSuccess(userInfo: YvpUserResponse) {
                    displayUserGreeting(userInfo)
                }

                override fun onError(message: String) {
                    Log.e(TAG, "Error fetching user info: $message")
                }
            })
        }
    }

    /**
     * Loads the verse of the day from the YouVersion API.
     * Now uses the static token by default, but can also use
     * authenticated token if available.
     * 
     * @param translationId The translation ID to use (defaults to NIV)
     */
    fun loadVerse(translationId: Int = DEFAULT_TRANSLATION_ID) {
        // Always use the static token for verse loading
        loadVerseWithStaticToken(translationId)
    }

    /**
     * Displays the verse data in the view.
     *
     * @param verse The Bible verse response to display
     */
    private fun displayVerse(verse: YvpBibleVerseResponse) {
        // Extract reference from USFM (e.g., "JHN.1.1" -> "John 1:1")
        val reference = formatUsfmToReference(verse.usfm)
        
        tvReference.text = reference
        tvVerseText.text = verse.text
        tvTranslation.text = getTranslationName(verse.translationId)
        
        verseContentLayout.visibility = View.VISIBLE
        tvError.visibility = View.GONE
    }

    /**
     * Gets a readable translation name from its ID.
     * 
     * @param translationId The translation ID
     * @return The readable translation name
     */
    private fun getTranslationName(translationId: Int): String {
        return when(translationId) {
            1 -> "KJV"
            59 -> "ESV"
            111 -> "NIV"
            else -> "Translation #$translationId"
        }
    }

    /**
     * Formats a USFM code to a readable reference.
     * 
     * @param usfm The USFM code (e.g., "JHN.1.1")
     * @return A formatted reference (e.g., "John 1:1")
     */
    private fun formatUsfmToReference(usfm: String): String {
        try {
            val parts = usfm.split(".")
            if (parts.size != 3) return usfm
            
            val book = when (parts[0]) {
                "GEN" -> "Genesis"
                "EXO" -> "Exodus"
                "LEV" -> "Leviticus"
                "NUM" -> "Numbers"
                "DEU" -> "Deuteronomy"
                "JOS" -> "Joshua"
                "JDG" -> "Judges"
                "RUT" -> "Ruth"
                "1SA" -> "1 Samuel"
                "2SA" -> "2 Samuel"
                "1KI" -> "1 Kings"
                "2KI" -> "2 Kings"
                "1CH" -> "1 Chronicles"
                "2CH" -> "2 Chronicles"
                "EZR" -> "Ezra"
                "NEH" -> "Nehemiah"
                "EST" -> "Esther"
                "JOB" -> "Job"
                "PSA" -> "Psalm"
                "PRO" -> "Proverbs"
                "ECC" -> "Ecclesiastes"
                "SNG" -> "Song of Solomon"
                "ISA" -> "Isaiah"
                "JER" -> "Jeremiah"
                "LAM" -> "Lamentations"
                "EZK" -> "Ezekiel"
                "DAN" -> "Daniel"
                "HOS" -> "Hosea"
                "JOL" -> "Joel"
                "AMO" -> "Amos"
                "OBA" -> "Obadiah"
                "JON" -> "Jonah"
                "MIC" -> "Micah"
                "NAM" -> "Nahum"
                "HAB" -> "Habakkuk"
                "ZEP" -> "Zephaniah"
                "HAG" -> "Haggai"
                "ZEC" -> "Zechariah"
                "MAL" -> "Malachi"
                "MAT" -> "Matthew"
                "MRK" -> "Mark"
                "LUK" -> "Luke"
                "JHN" -> "John"
                "ACT" -> "Acts"
                "ROM" -> "Romans"
                "1CO" -> "1 Corinthians"
                "2CO" -> "2 Corinthians"
                "GAL" -> "Galatians"
                "EPH" -> "Ephesians"
                "PHP" -> "Philippians"
                "COL" -> "Colossians"
                "1TH" -> "1 Thessalonians"
                "2TH" -> "2 Thessalonians"
                "1TI" -> "1 Timothy"
                "2TI" -> "2 Timothy"
                "TIT" -> "Titus"
                "PHM" -> "Philemon"
                "HEB" -> "Hebrews"
                "JAS" -> "James"
                "1PE" -> "1 Peter"
                "2PE" -> "2 Peter"
                "1JN" -> "1 John"
                "2JN" -> "2 John"
                "3JN" -> "3 John"
                "JUD" -> "Jude"
                "REV" -> "Revelation"
                else -> parts[0]
            }
            
            return "$book ${parts[1]}:${parts[2]}"
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting USFM: $usfm", e)
            return usfm
        }
    }

    /**
     * Shows or hides the loading indicator and other UI elements.
     *
     * @param isLoading Whether content is currently loading
     */
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        // Only hide verse content during loading, not the whole UI
        if (isLoading) {
            verseContentLayout.visibility = View.GONE
        } else {
            verseContentLayout.visibility = View.VISIBLE
            
            // Make sure login button is hidden if user is logged in
            if (repository.isAuthenticated()) {
                btnLogin.visibility = View.GONE
            }
        }
        
        tvError.visibility = View.GONE
    }

    /**
     * Shows an error message.
     *
     * @param message The error message to display
     */
    private fun showError(message: String) {
        // Use predefined error messages when possible
        val errorText = when {
            message.contains("Network") || message.contains("connection") -> 
                context.getString(R.string.yvp_network_error)
            message.contains("Authentication") || message.contains("auth") ->
                context.getString(R.string.yvp_auth_failed)
            else -> message
        }
        
        tvError.text = errorText
        tvError.visibility = View.VISIBLE
        verseContentLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        
        // Show login button on errors unless already authenticated
        btnLogin.visibility = if (repository.isAuthenticated()) View.GONE else View.VISIBLE
    }
} 