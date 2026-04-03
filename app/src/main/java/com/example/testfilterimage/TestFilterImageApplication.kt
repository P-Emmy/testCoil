package com.example.testfilterimage

import android.app.Application
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.util.DebugLogger

class TestFilterImageApplication : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application created")
    }

    override fun newImageLoader(context: coil3.PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .logger(DebugLogger())
            .build()
    }

    companion object {
        private const val TAG = "TestFilterImageApp"
    }
}
