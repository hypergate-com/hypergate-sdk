package com.hypergate.sdk

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import java.util.concurrent.CountDownLatch

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class VolleySampleTest {

    @get:Rule
    val activityRule = ActivityTestRule(Activity::class.java)

    @Test
    fun testVolleyRequest() {
        val countDownLatch = CountDownLatch(1)
        val queue = Volley.newRequestQueue(activityRule.activity)
        val url = "https://securedendpoint.com"
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
            },
            Response.ErrorListener { Log.d("ERROR", "That didn't work!") }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = super.getHeaders()
                val token = Hypergate.requestTokenSync(
                    activityRule.activity,
                    "HTTP/${Uri.parse(this.url).host}"
                )
                headers.put("Authorization", "Negotiate ${token}")
                return super.getHeaders()
            }
        }
        queue.add(stringRequest)
        countDownLatch.await()
    }
}
