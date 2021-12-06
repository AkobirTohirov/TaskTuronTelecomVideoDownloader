package com.example.taskturon.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.taskturon.R

abstract class BaseActivity(@LayoutRes private val layoutId: Int) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initialize(savedInstanceState)
    }

    abstract fun initialize(savedInstanceState: Bundle?)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragmentsActivityResults(requestCode, resultCode, data)
    }

    private fun fragmentsActivityResults(requestCode: Int, resultCode: Int, data: Intent?) {
        val navHostFr = supportFragmentManager.findFragmentById(R.id.navHostFragment)
        val fragments = navHostFr?.childFragmentManager?.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}