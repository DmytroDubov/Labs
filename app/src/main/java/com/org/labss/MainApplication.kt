package com.org.labss

import android.app.Application
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig


class MainApplication : Application() {

    companion object {
        const val POSTHOG_API_KEY = "phc_qD9wuHBoeqTkMF9csHrirqu1SubhC1KL3uC1oHbndyB"
        const val POSTHOG_HOST = "https://eu.i.posthog.com"
    }

    override fun onCreate() {
        super.onCreate()

        val config = PostHogAndroidConfig(
            apiKey = POSTHOG_API_KEY,
            host = POSTHOG_HOST

        ).apply { debug = true }

        config.sessionReplay = true
        config.sessionReplayConfig.maskAllTextInputs = true
        config.sessionReplayConfig.maskAllImages = true
        config.sessionReplayConfig.captureLogcat = true
        config.sessionReplayConfig.screenshot = true
        config.sessionReplayConfig.throttleDelayMs = 1000
        config.sessionReplayConfig.sampleRate = null

        PostHogAndroid.setup(this, config)

    }
}