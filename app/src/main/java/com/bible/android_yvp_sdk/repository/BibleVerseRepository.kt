package com.bible.android_yvp_sdk.repository

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bible.android_yvp_sdk.model.BibleVerseResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * Repository for fetching Bible verses from a Google Sheets CSV file.
 */
class BibleVerseRepository {
    companion object {
        private const val TAG = "BibleVerseRepository"
        
        // Default Google Sheets URL with Bible verses
        private var SHEETS_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRjGBuePH0VQFVWRu_H_iDCr6GuSKMO6JOO_gsMwgJ3iOlqyJ5JgGQe0VKsEdpw5ua05-JSDAvIxKK7/pub?output=csv"
        
        /**
         * Configure the repository with custom settings.
         *
         * @param sheetsUrl The Google Sheets CSV URL to fetch verses from
         */
        @JvmStatic
        fun configure(sheetsUrl: String) {
            SHEETS_URL = sheetsUrl
            Log.d(TAG, "Repository configured with custom URL: $SHEETS_URL")
        }
    }

    private val client: OkHttpClient
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        Log.d(TAG, "Initializing BibleVerseRepository")
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createDummyCall(request: Request): Call<BibleVerseResponse> {
        return object : Call<BibleVerseResponse> {
            override fun clone(): Call<BibleVerseResponse> = this
            override fun execute(): Response<BibleVerseResponse> = throw UnsupportedOperationException()
            override fun enqueue(callback: Callback<BibleVerseResponse>) {}
            override fun isExecuted(): Boolean = true
            override fun cancel() {}
            override fun isCanceled(): Boolean = false
            override fun request(): okhttp3.Request = request
            override fun timeout(): Timeout = Timeout.NONE
        }
    }

    /**
     * Fetch a Bible verse from the configured Google Sheets CSV.
     *
     * @param reference The Bible verse reference to fetch (e.g., "john 3:16")
     * @param callback The callback to receive the result
     */
    fun getVerse(reference: String, callback: Callback<BibleVerseResponse>) {
        Log.d(TAG, "Making request for verse: $reference")
        
        val request = Request.Builder()
            .url(SHEETS_URL)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e(TAG, "Failed to fetch CSV: ${e.message}")
                mainHandler.post {
                    callback.onFailure(createDummyCall(request), e)
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    val csvContent = response.body?.string()
                    Log.d(TAG, "CSV response: $csvContent")
                    
                    if (csvContent != null) {
                        // Parse the CSV content properly to handle commas in the verse text
                        val firstLine = csvContent.lines().first()
                        Log.d(TAG, "Parsing first line: $firstLine")
                        
                        // The format is: Reference,"Verse text"
                        val commaIndex = firstLine.indexOf(",\"")
                        if (commaIndex > 0) {
                            // Extract the reference and verse
                            val parsedRef = firstLine.substring(0, commaIndex)
                            val quotedVerse = firstLine.substring(commaIndex + 1)
                            
                            Log.d(TAG, "Parsed reference: $parsedRef")
                            Log.d(TAG, "Quoted verse: $quotedVerse")
                            
                            // Remove the quotes from verse
                            val verse = quotedVerse.trim('"')
                            
                            Log.d(TAG, "Final verse text: $verse")
                            
                            val verseResponse = BibleVerseResponse(
                                reference = parsedRef,
                                verses = listOf(
                                    BibleVerseResponse.Verse(
                                        bookId = "JHN",
                                        bookName = "John",
                                        chapter = 3,
                                        verse = 16,
                                        text = verse
                                    )
                                ),
                                text = verse,
                                translationId = "csv",
                                translationName = "CSV Version",
                                translationNote = "From Google Sheets"
                            )
                            mainHandler.post {
                                callback.onResponse(createDummyCall(request), Response.success(verseResponse))
                            }
                        } else {
                            Log.e(TAG, "Cannot find comma+quote pattern in CSV: $firstLine")
                            mainHandler.post {
                                callback.onFailure(createDummyCall(request), Exception("Invalid CSV format"))
                            }
                        }
                    } else {
                        Log.e(TAG, "Empty response")
                        mainHandler.post {
                            callback.onFailure(createDummyCall(request), Exception("Empty response"))
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing CSV: ${e.message}")
                    mainHandler.post {
                        callback.onFailure(createDummyCall(request), e)
                    }
                } finally {
                    response.close()
                }
            }
        })
    }
}