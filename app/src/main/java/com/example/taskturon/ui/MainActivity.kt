package com.example.taskturon.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.taskturon.R
import com.example.taskturon.base.BaseActivity
import com.example.taskturon.utils.extensions.gone
import com.example.taskturon.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : BaseActivity(R.layout.activity_main) {

    val sharedViewModel: SharedViewModel by viewModels()

    override fun initialize(savedInstanceState: Bundle?) {
        navigateIfNeed(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateIfNeed(intent)
    }

    private fun navigateIfNeed(intent: Intent?) {

    }

    var isLoading: Boolean
        get() = pbMain.isVisible
        set(value) {
            if (value) pbMain.visible() else pbMain.gone()
        }
}