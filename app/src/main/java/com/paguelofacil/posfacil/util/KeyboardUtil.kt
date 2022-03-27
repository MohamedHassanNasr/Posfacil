package com.paguelofacil.posfacil.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.roundToInt


/**
 * Keyboard utility class
 *
 * @constructor Create empty Keyboard util
 */
object KeyboardUtil {

    fun isKeyboardClosed(rootView: View): Boolean {
        return !isKeyboardOpen(rootView)
    }

    fun hideKeyboard(context: Context?, view: View?) {
        val inputMethodManager: InputMethodManager? = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    fun showKeyboard(context: Context?, view: View?) {
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let {
            inputMethodManager.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideKeyboard(activity: Activity?) {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showKeyboard(activity: Activity?) {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun isKeyboardOpen(view: View?): Boolean {
        val visibleBounds = Rect()
        view?.let {
            view.getWindowVisibleDisplayFrame(visibleBounds)
            val heightDiff = view.height - visibleBounds.height()
            val marginOfError = convertDpToPx(view.context, 50F).roundToInt()
            return heightDiff > marginOfError
        }
        return false
    }

    private fun convertDpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

}