package com.bible.android_yvp_sdk.ui;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bible.android_yvp_sdk.R

/**
 * A Fragment that displays a Bible verse from YouVersion with authentication.
 * 
 * This fragment provides an easy way to integrate YouVersion Bible verses into your app
 * using the fragment-based architecture. It wraps the YvpBibleVerseView component
 * and handles the fragment lifecycle.
 * 
 * Usage example:
 * ```kotlin
 * // In your activity
 * val fragment = YvpBibleVerseFragment.newInstance(111) // NIV translation
 * supportFragmentManager.beginTransaction()
 *     .replace(R.id.fragmentContainer, fragment)
 *     .commit()
 * 
 * // Later, update the translation if needed
 * fragment.updateTranslation(59) // ESV translation
 * ```
 * 
 * XML layout example:
 * ```xml
 * <FrameLayout
 *     android:id="@+id/fragmentContainer"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content" />
 * ```
 */
class YvpBibleVerseFragment : Fragment() {
    private lateinit var yvpBibleVerseView: YvpBibleVerseView
    private var translationId: Int = 111 // Default to NIV

    companion object {
        private const val ARG_TRANSLATION_ID = "translation_id"

        /**
         * Creates a new instance of YvpBibleVerseFragment with the specified translation ID.
         *
         * @param translationId The translation ID to use (e.g., 111 for NIV)
         * @return A new instance of YvpBibleVerseFragment
         */
        fun newInstance(translationId: Int = 111): YvpBibleVerseFragment {
            val fragment = YvpBibleVerseFragment()
            val args = Bundle().apply {
                putInt(ARG_TRANSLATION_ID, translationId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            translationId = it.getInt(ARG_TRANSLATION_ID, translationId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_yvp_bible_verse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yvpBibleVerseView = view.findViewById(R.id.yvpBibleVerseView)
        loadVerse()
    }

    /**
     * Loads the verse with the current translation ID.
     */
    private fun loadVerse() {
        yvpBibleVerseView.loadVerse(translationId)
    }

    /**
     * Updates the translation ID and reloads the verse if the view is initialized.
     *
     * @param translationId The new translation ID to use
     */
    fun updateTranslation(translationId: Int) {
        this.translationId = translationId
        if (::yvpBibleVerseView.isInitialized) {
            loadVerse()
        }
    }
} 