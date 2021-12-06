package com.example.taskturon.ui

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.taskturon.R
import com.example.taskturon.base.BaseFragment
import com.example.taskturon.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.screen_bottom_nav.*

@AndroidEntryPoint
class BottomNavScreen : BaseFragment(R.layout.screen_bottom_nav) {

    override val viewModel: BaseViewModel by viewModels()

    override fun initialize() {
        setupBottomNavigation()
        btMenu.setOnClickListener(this)
    }

    private fun setupBottomNavigation() {
        val navHostFragment: NavHostFragment =
            childFragmentManager.findFragmentById(R.id.bottomNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNav, navController)
        navController.addOnDestinationChangedListener { _, _, _ ->
            hideKeyboard()
        }
        bottomNav.setOnNavigationItemReselectedListener { }
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.btMenu) {

        }
    }
}