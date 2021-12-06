package com.example.taskturon.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.taskturon.R
import com.example.taskturon.ui.BottomNavScreenDirections
import com.example.taskturon.ui.MainActivity
import com.example.taskturon.ui.SharedViewModel
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment(@LayoutRes layoutId: Int) :
    Fragment(layoutId), View.OnClickListener {

    protected abstract val viewModel: BaseViewModel

    private lateinit var sharedViewModel: SharedViewModel
    private var position = -1

    protected open val navController: NavController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        setStatusBarColor(R.color.black)
        sharedViewModel = (requireActivity() as MainActivity).sharedViewModel
        viewModel.navigate.observe(viewLifecycleOwner, navigateObserver)
        initialize()
    }

    private val navigateObserver = Observer<NavCommand> { command ->
        when (command) {

            is NavCommand.HOME -> navController.navigate(BottomNavScreenDirections.globalHome())
            is NavCommand.BACK -> navController.popBackStack()
            is NavCommand.To -> navController.navigate(command.direction)
            is NavCommand.BackTo -> navController.popBackStack(
                command.destinationId,
                command.inclusive
            )
        }

    }


    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    protected fun hideKeyboard() {
        val manager: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null)
            manager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected fun setStatusBarColor(@ColorRes color: Int) {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), color)
    }

    abstract fun initialize()

    fun message(message: String, isError: Boolean = true, duration: Int = Snackbar.LENGTH_LONG) {
        if (activity != null && message.isNotEmpty()) {
            val snackBar = Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                "",
                duration
            )
            snackBar.setActionTextColor(Color.WHITE)
            snackBar.setText(message)
            val sbView = snackBar.view
            var color: Int = R.color.colorRed
            if (!isError) {
                color = R.color.colorGreyB3
            }
            sbView.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
            snackBar.show()
        }
    }

    override fun onClick(p0: View?) {

    }

    protected var isLoading: Boolean
        set(value) {
            (activity as MainActivity).isLoading = value
        }
        get() = (activity as MainActivity).isLoading

    private var errorShown = false

}