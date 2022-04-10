package com.paguelofacil.posfacil.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import com.androidadvance.topsnackbar.TSnackbar
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.data.network.api.ApiResponseObserver
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.ui.view.account.activities.LoginActivity
import com.paguelofacil.posfacil.util.Constantes.LoadingState
import com.paguelofacil.posfacil.util.KeyboardUtil
import com.paguelofacil.posfacil.util.LoadingDialog
import timber.log.Timber
import java.io.File
import java.io.IOException


/**
 * Base class that should be implemented by every activity
 */
abstract class BaseActivity : AppCompatActivity(), ApiResponseObserver<Any> {
    private lateinit var keyboardListener: ViewTreeObserver.OnGlobalLayoutListener
    private var keyboardStateChangeListener: (Boolean) -> Unit = { }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var progressDialog: LoadingDialog
    private var baseViewModel: BaseViewModel? = null
    private lateinit var appDialog: Dialog
    protected var outputUri: Uri? = null
    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialiseProgressDialog()
        initialiseGestureListeners()
        initialiseKeyboardListener()
        setOneTapToCloseKeyboard(getRootView())
    }

    override fun onResume() {
        super.onResume()
        //add keyboard listener to know when when keyboard opens and close
        getRootView()?.viewTreeObserver?.addOnGlobalLayoutListener(keyboardListener)
    }

    override fun onPause() {
        super.onPause()
        //remove keyboard listener
        getRootView()?.viewTreeObserver?.removeOnGlobalLayoutListener(keyboardListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //pass the touch event to the gesture detector to decide the touch gesture
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    /**
     * Initialise progress dialog to be used
     */
    private fun initialiseProgressDialog() {
        progressDialog = LoadingDialog(this)
        appDialog = Dialog(this)
        appDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        appDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * Initialise gesture listeners and hide the keyboard when callback received
     *
     */
    private fun initialiseGestureListeners() {
        gestureDetector = GestureDetectorCompat(this, MyGestureListener {
            KeyboardUtil.hideKeyboard(this)
        })
    }

    /**
     * My gesture listener to get callbacks when user scrolls ad single tap on the screen
     *
     * @property callback
     * @constructor Create empty My gesture listener
     */
    private class MyGestureListener(val callback: () -> Unit) :
        GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            //pass the event to the callback
            callback.invoke()
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            callback.invoke()
            return super.onSingleTapConfirmed(e)
        }
    }

    /**
     * Get root view from the base layout
     *
     * @return
     */
    private fun getRootView(): View? {
        val view: View? = findViewById(android.R.id.content)
        if (view == null) {
            debugLog("view is null")
        }
        return view
    }

    fun debugLog(message: String?) {
        Timber.d(message)
    }

    fun showToastLong(message: CharSequence?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun showToastShort(message: CharSequence?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressDialog() {
        if (!isDestroyed && !progressDialog.isShowing) {
            progressDialog.show()
        }
    }

    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    /**
     * Show snackbar from the top
     *
     * @param msg to be displayed
     */
    fun showSnack(msg: String?) {
        if (msg.isNullOrEmpty()) {
            return
        }
        val snackBar =
            TSnackbar.make(findViewById(android.R.id.content), msg, TSnackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.setIconLeft(R.drawable.ic_exclamation_inside_white_cicle, 16f)
        snackBar.setIconPadding(32)
        val snackBarView = snackBar.view
        snackBarView.background =
            ContextCompat.getDrawable(this, R.drawable.dr_rect_grey_999999_filled_rounded_corner)
        val textView = snackBarView.findViewById<View>(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.maxLines = 5

        val params: FrameLayout.LayoutParams = snackBarView.layoutParams as FrameLayout.LayoutParams
        params.setMargins(
            params.leftMargin + 32,
            params.topMargin + 16,
            params.rightMargin + 32,
            params.bottomMargin
        )

        snackBarView.layoutParams = params
        snackBar.show()
    }

    /**
     * Pass the base viewmodel instance from the child class
     *
     * @param viewModel
     */
    protected fun setBaseViewModel(viewModel: BaseViewModel) {
        baseViewModel = viewModel
        baseViewModel?.loadingState?.observe(this@BaseActivity) { state ->
            if (state != null) {
                when (state) {
                    LoadingState.LOADING -> showProgressDialog()
                    LoadingState.LOADED -> hideProgressDialog()
                }
            }
        }
    }


    /**
     * Success api response callback
     *
     * @param data
     * @param responseCode
     * @param requestCode
     * @param msg
     */
    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {

    }

    /**
     * Exception callback after api call
     *
     * @param exception
     * @param requestCode
     */
    override fun onException(requestCode: Int, exception: ApiError) {
        baseViewModel?.setLoadingState(LoadingState.LOADED)
        showSnack(exception.message ?: getString(R.string.something_went_wrong))
        when (exception.code) {
            ApiRequestCode.USER_NOT_LOGGED_IN, ApiRequestCode.SESSION_EXPIRED1, ApiRequestCode.SESSION_EXPIRED2, ApiRequestCode.SESSION_EXPIRED3,
            ApiRequestCode.SESSION_EXPIRED4, ApiRequestCode.SESSION_EXPIRED5, ApiRequestCode.SESSION_EXPIRED6,
            -> {
                Handler(Looper.myLooper()!!).postDelayed({
                    logOutUser()
                }, 1000)
            }
        }
    }

    private fun logOutUser() {
        val user = UserRepo.getUser()
        user.loggedIn = false
        UserRepo.setOrUpdateUser(user)

        finishAffinity()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    /**
     * No internet connection api callback
     *
     * @param requestCode
     * @param msg
     */
    override fun noInternetConnection(requestCode: Int, msg: String?) {
        baseViewModel?.setLoadingState(LoadingState.LOADED)
        showSnack(msg ?: getString(R.string.something_went_wrong))
    }

    /**
     * utility method to set and remove the tick drawable at the end of an ediText.
     * Set when Edittext data is validated and edittext is out of focus else removes
     * the tick drawable
     *
     * @param ediText for which need to device if tick mark should be shown or hide
     * @param isValidated true if data on the editText is validated. false otherwise
     * @param hasFocus true id the editText has focus
     */
    fun updateTickMark(ediText: EditText, isValidated: Boolean, hasFocus: Boolean) {
        if (!hasFocus && isValidated) {
            ediText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(this, R.drawable.ic_check_green),
                null
            )
        } else {
            ediText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    /**
     * Set when keyboard needs to be closed when user taps once on the screen
     * anywhere outside an editText
     *
     * @param view on which needs to calculate track the touch events
     */
    @SuppressLint("ClickableViewAccessibility")
    protected open fun setOneTapToCloseKeyboard(view: View?) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view?.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                false
            }
        }
        //If a layout container, iterate over children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setOneTapToCloseKeyboard(innerView)
            }
        }
    }

    /**
     * Initialise the keyboard open close event listener
     *
     */
    private fun initialiseKeyboardListener() {
        keyboardListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            // Keep a reference to the last state of the keyboard
            private var lastState: Boolean = KeyboardUtil.isKeyboardOpen(getRootView())

            /**
             * Something in the layout has changed
             * so check if the keyboard is open or closed
             * and if the keyboard state has changed
             * save the new state and invoke the callback
             */
            override fun onGlobalLayout() {
                val isOpen = KeyboardUtil.isKeyboardOpen(getRootView())
                if (isOpen == lastState) {
                    return
                } else {
                    lastState = isOpen
                    keyboardStateChangeListener.invoke(isOpen)
                }
            }
        }
    }


    /**
     * Set keyboard state(open/close) change listener
     *
     * @param keyboardStateChangeListener
     * @receiver
     */
    fun setKeyboardStateChangeListener(keyboardStateChangeListener: (Boolean) -> Unit) {
        this.keyboardStateChangeListener = keyboardStateChangeListener
    }

    override fun onDestroy() {
        hideProgressDialog()
        super.onDestroy()
    }
}