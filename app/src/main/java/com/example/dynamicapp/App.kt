package com.example.dynamicapp

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat

// or extend SplitCompatApplication
class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}