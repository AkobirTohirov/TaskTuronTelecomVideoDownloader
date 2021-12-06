package com.example.taskturon.utils.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun activityNavController(@IdRes viewId: Int): ActivityNavControllerDelegate =
    ActivityNavControllerDelegate(viewId)

class ActivityNavControllerDelegate(@IdRes private val viewId: Int) :
    ReadOnlyProperty<Fragment, NavController> {
    override fun getValue(thisRef: Fragment, property: KProperty<*>): NavController {
        return thisRef.requireActivity().findNavController(viewId)
    }
}
