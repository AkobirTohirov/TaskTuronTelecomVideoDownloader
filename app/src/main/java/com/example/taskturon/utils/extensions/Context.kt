package com.example.taskturon.utils.extensions

import android.content.Context
import java.io.File

val Context.externalOfflineDir: String
    get() {
        val dir = "${externalCacheDir?.absolutePath}/offline"
        val f = File(dir)
        if (!f.exists()) {
            f.mkdirs()
        }
        return dir
    }