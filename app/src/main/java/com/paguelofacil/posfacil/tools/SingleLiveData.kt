package com.paguelofacil.posfacil.tools

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Single live data. An observable data wrapper class similar to MutableLiveData
 * with only difference is that it only allows one active observer at a time.
 * Multiple observers can observe a SingleLiveData at a time but only the active observer will
 * observe the change event
 *
 * @param T data type to wrap
 * @constructor Create empty Single live data
 */
class SingleLiveData<T> : MutableLiveData<T?>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        // Observe the internal MutableLiveData
        super.observe(owner, { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    /**
     * Set value on the main thread
     *
     * @param t value
     */
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Post value on a different thread that the Mains
     *
     * @param value
     */
    override fun postValue(value: T?) {
        mPending.set(true)
        super.postValue(value)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}