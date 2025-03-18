package com.bible.android_yvp_sdk.ui;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bible.android_yvp_sdk.R

/**
 * A Fragment that displays a Bible verse.
 * 
 * This fragment provides an easy way to integrate Bible verses into your app
 * using the fragment-based architecture. It wraps the BibleVerseView component
 * and handles the fragment lifecycle.
 * 
 * Usage example:
 * ```kotlin
 * // In your activity
 * val fragment = BibleVerseFragment.newInstance("john 3:16")
 * supportFragmentManager.beginTransaction()
 *     .replace(R.id.fragmentContainer, fragment)
 *     .commit()
 * 
 * // Later, update the verse if needed
 * fragment.updateVerseReference("romans 8:28")
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
class BibleVerseFragment : Fragment() {
    private lateinit var bibleVerseView: BibleVerseView
    private var verseReference: String = "john 3:16" // Default reference

    companion object {
        private const val ARG_VERSE_REFERENCE = "verse_reference"

        /**
         * Creates a new instance of BibleVerseFragment with the specified verse reference.
         *
         * @param verseReference The Bible verse reference to display (e.g., "john 3:16")
         * @return A new instance of BibleVerseFragment
         */
        fun newInstance(verseReference: String): BibleVerseFragment {
            val fragment = BibleVerseFragment()
            val args = Bundle().apply {
                putString(ARG_VERSE_REFERENCE, verseReference)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            verseReference = it.getString(ARG_VERSE_REFERENCE, verseReference)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_bible_verse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bibleVerseView = view.findViewById(R.id.bibleVerseView)
        loadVerse()
    }

    /**
     * Loads the current verse reference into the view.
     */
    private fun loadVerse() {
        bibleVerseView.loadVerse(verseReference)
    }

    /**
     * Updates the verse reference and reloads the verse if the view is initialized.
     *
     * @param reference The new Bible verse reference to display
     */
    fun updateVerseReference(reference: String) {
        verseReference = reference
        if (::bibleVerseView.isInitialized) {
            loadVerse()
        }
    }
}
