package com.hypergate.sdk

import android.app.Activity
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import okhttp3.OkHttpClient
import okhttp3.Request


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class OkHttpSampleTest {

    @get:Rule
    val activityRule = ActivityTestRule(Activity::class.java)

    @Test
    fun testOkHttpRequest() {
        val client = OkHttpClient.Builder()
            .addInterceptor(HypergateOkHttpInterceptor(activityRule.activity))
            .build()

        val request = Request.Builder()
            .url("https://securedendpoint.com")
            .build()

        val response = client.newCall(request).execute()
    }
}
