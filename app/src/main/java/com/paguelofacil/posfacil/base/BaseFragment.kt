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
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
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
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * Base fragment class. Should be inherited by all the fragments for common architecture
 * and accessing some utility functions
 */
abstract class BaseFragment : Fragment(), ApiResponseObserver<Any> {
    private val TAG = BaseFragment::class.simpleName
    private lateinit var keyboardListener: ViewTreeObserver.OnGlobalLayoutListener
    private var keyboardStateChangeListener: (Boolean) -> Unit = { }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var mAppDialog: Dialog
    private lateinit var progressDialog: LoadingDialog
    protected var outputUri: Uri? = null
    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
        getRootView()?.viewTreeObserver?.removeOnGlobalLayoutListener(keyboardListener)
    }

    private fun initialiseProgressDialog() {
        progressDialog = LoadingDialog(requireContext())
        mAppDialog = Dialog(requireContext())
        mAppDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mAppDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initialiseGestureListeners() {
        gestureDetector = GestureDetectorCompat(requireContext(), MyGestureListener {
            try {
                KeyboardUtil.hideKeyboard(requireActivity())
            } catch (e: Exception) {
                Timber.e(e)
            }
        })
    }

    private class MyGestureListener(val callback: () -> Unit) :
        GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            callback.invoke()
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            callback.invoke()
            return super.onSingleTapConfirmed(e)
        }
    }

    private fun getRootView(): View? {
        return try {
            val view: View? = requireActivity().findViewById(android.R.id.content)
            if (view == null) {
                Timber.d("view for keyboard is null")
            }
            view
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    fun debugLog(message: String?) {
        Timber.d(message)
    }

    protected fun setBaseViewModel(viewModel: BaseViewModel) {
        this.baseViewModel = viewModel
        baseViewModel.loadingState.observe(this@BaseFragment, { state ->
            if (state != null) {
                when (state) {
                    LoadingState.LOADING -> showProgressDialog()
                    LoadingState.LOADED -> hideProgressDialog()
                }
            }
        })
    }

    fun showToastLong(message: CharSequence?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun showToastShort(message: CharSequence?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnack(msg: String?) {
        if (msg.isNullOrEmpty()) {
            return
        }
        val snackBar = TSnackbar.make(
            requireActivity().findViewById(android.R.id.content),
            msg,
            TSnackbar.LENGTH_LONG
        )
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.setIconLeft(R.drawable.ic_exclamation_inside_white_cicle, 16f)
        snackBar.setIconPadding(32)
        val snackBarView = snackBar.view
        snackBarView.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.dr_rect_grey_999999_filled_rounded_corner
        )
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

    private fun showProgressDialog() {
        if (isVisible && !progressDialog.isShowing) {
            progressDialog.show()
        }
    }

    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    override fun onResponseSuccess(requestCode: Int, responseCode: Int, msg: String?, data: Any?) {

    }

    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {


    }

    protected open fun onFailure(failureResponse: Throwable?) {
        baseViewModel.setLoadingState(LoadingState.LOADED)
        if (failureResponse != null && (failureResponse is SocketTimeoutException || failureResponse is UnknownHostException || failureResponse is ConnectException)) {
            showSnack(resources.getString(R.string.something_went_wrong))
        } else {
            Timber.d(failureResponse)
            showSnack(failureResponse?.message)
        }
    }

    override fun onException(requestCode: Int, exception: ApiError) {
        baseViewModel.setLoadingState(LoadingState.LOADED)
        showSnack(exception.message)
        when (exception.code) {
            ApiRequestCode.USER_NOT_LOGGED_IN, ApiRequestCode.SESSION_EXPIRED1, ApiRequestCode.SESSION_EXPIRED2, ApiRequestCode.SESSION_EXPIRED3,
            ApiRequestCode.SESSION_EXPIRED4, ApiRequestCode.SESSION_EXPIRED5, ApiRequestCode.SESSION_EXPIRED6,
            -> {
                Handler(Looper.myLooper()!!).postDelayed({
                    logOutUser()
                }, 1500)
            }
        }
    }


    private fun logOutUser() {
        activity?.let {
            val user = UserRepo.getUser()
            user.loggedIn = false
            UserRepo.setOrUpdateUser(user)

            val intent = Intent(it, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            it.finishAffinity()
        }
    }

    override fun noInternetConnection(requestCode: Int, msg: String?) {
        baseViewModel.setLoadingState(LoadingState.LOADED)
        showToastLong(msg)
    }



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

    fun setKeyboardStateChangeListener(keyboardStateChangeListener: (Boolean) -> Unit) {
        this.keyboardStateChangeListener = keyboardStateChangeListener
    }

    fun updateTickMark(et: EditText, validated: Boolean, hasFocus: Boolean) {
        if (!hasFocus && validated) {
            et.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_green),
                null
            )
        } else {
            et.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    override fun onDestroyView() {
        hideProgressDialog()
        super.onDestroyView()
    }
}